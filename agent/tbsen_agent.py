import asyncio
import grpc.aio
import time
import os
import uuid
import socket

# import executor & parser logic
from tbsen_executor import TbsenExecutor
from tbsen_parser import TbsenParser

# import proto
import filter_pb2
import filter_pb2_grpc

# 설정 (Back-end -> netty 8081 Port)
SERVER_ADDR = "192.168.0.11:8081" # TODO: 추후 .env 파일에서 IP 로드 하도록 예정

# Host Identity
UUID_DIR = "/etc/tbsen-agent"
HOST_UUID_FILE = f"{UUID_DIR}/agent-uuid"
HOSTNAME = socket.gethostname()

# 임시 - 확실하게 구분 가능한 인터페이스 IP 추출 가능한 경우에 로직추가
HOST_IP = "127.0.0.1" 

if not os.path.exists(UUID_DIR):
    try:
        os.makedirs(UUID_DIR, exist_ok=True)
    except PermissionError:
        print(f"[ERROR] UUID: {UUID_DIR} 디렉터리를 생성할 권한이 없습니다.")
        exit(1)

if not os.path.exists(HOST_UUID_FILE):
    agent_uuid = str(uuid.uuid4())
    try:
        with open(HOST_UUID_FILE, "w") as f:
            f.write(agent_uuid)
            print(f"[INFO] UUID: {agent_uuid} 파일 생성 완료")
    except PermissionError:
        print(f"[ERROR] UUID: 파일 쓰기 권한이 없습니다.")
        exit(1)
else:
    with open(HOST_UUID_FILE, "r") as f:
        agent_uuid = f.read().strip()
        print(f"[INFO] UUID: {agent_uuid} 파일 읽기 성공")


# Command Listener
async def run_command_listener(stub, executor):
    identity = filter_pb2.AgentIdentity(
        uuid=agent_uuid, 
        hostname=HOSTNAME,
        agent_version="0.2.0",
        ip_address=HOST_IP # Placeholder 전송
    )
    print(f"[START] tableSentinel-Agent: {HOSTNAME} 명령 서비스 시작 (UUID: {agent_uuid})")

    try:
        stream = stub.SubscribeCommands(identity)

        async for cmd in stream:
            payload_type = cmd.WhichOneof('payload')
            cid = cmd.command_id
            print(f"[RECV] COMMAND ID: {cid} | Type: {payload_type}")

            is_success = False
            msg = ""

            try:
                match payload_type:
                    # XDP Logic
                    case "xdp":
                        xdp = cmd.xdp
                        mode = xdp.mode if xdp.mode else "src,dst"

                        if xdp.target_ip:
                            match xdp.action:
                                case filter_pb2.ACTION_ADD:
                                    await asyncio.to_thread(executor.add_xdp_ip_rule, xdp.target_ip, mode)
                                    msg = f"XDP IP Blocked: {xdp.target_ip}"
                                case filter_pb2.ACTION_DELETE:
                                    await asyncio.to_thread(executor.delete_xdp_ip_rule, xdp.target_ip, mode)
                                    msg = f"XDP IP Unblocked: {xdp.target_ip}"
                        
                        elif xdp.interface_name:
                             match xdp.action:
                                case filter_pb2.ACTION_ADD:
                                    await asyncio.to_thread(executor.load_xdp_interface, xdp.interface_name)
                                    msg = f"XDP Loaded on {xdp.interface_name}"
                                case filter_pb2.ACTION_DELETE:
                                    await asyncio.to_thread(executor.unload_force_xdp_interface, xdp.interface_name)
                                    msg = f"XDP Unloaded from {xdp.interface_name}"
                        
                        is_success = True

                    # NFTables Logic
                    case "nft":
                        nft = cmd.nft
                        match nft.action:
                            case filter_pb2.ACTION_ADD:
                                await asyncio.to_thread(executor.add_nft_drop_ip, nft.chain, nft.target_ip)
                                msg = f"NFT IP Dropped: {nft.target_ip}"
                            case filter_pb2.ACTION_DELETE:
                                msg = f"NFT Rule Deleted"
                        
                        is_success = True

                    case _:
                        msg = f"Unknown payload: {payload_type}"
                        is_success = False

            except Exception as e:
                is_success = False
                msg = f"Execution Error: {str(e)}"
                print(f"[FAIL] {msg}")

            # 결과 보고 (Ack)
            try:
                report = filter_pb2.CommandResponse(
                    command_id=cid,
                    success=is_success,
                    message=msg
                )
                await stub.ReportCommandResult(report)
                print(f"[REPORT] 결과 전송 완료: {msg}")
            except grpc.RpcError as e:
                print(f"[WARN] 결과 보고 실패: {e}")

    except grpc.RpcError as e:
        print(f"[ERROR] gRPC 연결 끊김: {e}, 5초 대기")
        await asyncio.sleep(5)


# Report Generator
async def generate_status_reports(executor):
    print(f"[START] 에이전트 리포트 스트림 시작")
    
    while True:
        xdp_task = asyncio.to_thread(executor.get_xdp_status)
        nft_task = asyncio.to_thread(executor.get_nft_ruleset)
        
        raw_xdp_result, raw_nft_result = await asyncio.gather(xdp_task, nft_task)

        xdp_proto_details = []
        nft_json_str = ""

        # XDP Parsing
        match raw_xdp_result:
            case (True, raw_data):
                match TbsenParser.parse_xdp_status(raw_data):
                    case (True, parsed_data):
                        for iface in parsed_data.get("interfaces", []):
                            stats = parsed_data.get("stats", {}).get(iface["name"], {})
                            xdp_proto_details.append(filter_pb2.XdpInterfaceInfo(
                                name=iface.get("name", "unknown"),
                                mode=iface.get("mode", "unknown"),
                                drop_count=int(stats.get("drop", 0)),
                                pass_count=int(stats.get("pass", 0))
                            ))
            case (False, err):
                pass 

        # NFT Parsing
        match raw_nft_result:
            case (True, raw_json):
                match raw_json:
                    case str(): nft_json_str = raw_json
                    case _: 
                        import json
                        nft_json_str = json.dumps(raw_json)

        yield filter_pb2.AgentStatus(
            uuid=agent_uuid,
            timestamp=int(time.time()),
            hostname=HOSTNAME,
            ip_address=HOST_IP, # Placeholder
            xdp_details=xdp_proto_details,
            xdp_mode=filter_pb2.XDP_MODE_NATIVE,
            nftables_raw_json=nft_json_str
        )

        await asyncio.sleep(5)


# Main Entry Point
async def main():
    executor = TbsenExecutor(use_sudo=True)
    
    async with grpc.aio.insecure_channel(SERVER_ADDR) as channel:
        stub = filter_pb2_grpc.FilterAgentStub(channel)
        
        # 에이전트 등록
        try:
            reg_resp = await stub.RegisterAgent(filter_pb2.AgentIdentity(
                uuid=agent_uuid,
                hostname=HOSTNAME,
                agent_version="0.2.0",
                ip_address=HOST_IP
            ))
            print(f"[INFO] 등록 성공: {reg_resp.message}")
        except grpc.RpcError as e:
            print(f"[WARN] 등록 실패 (서버 확인 필요): {e}")

        # 비동기로 작업 실행
        listener_task = asyncio.create_task(run_command_listener(stub, executor))
        reporter_task = asyncio.create_task(
            stub.ReportStatus(generate_status_reports(executor))
        )

        await asyncio.gather(listener_task, reporter_task)

if __name__ == "__main__":
    try:
        asyncio.run(main())
    except KeyboardInterrupt:
        print("[SHUTDOWN] 에이전트 종료")