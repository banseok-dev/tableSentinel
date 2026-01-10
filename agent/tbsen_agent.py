import asyncio
import grpc.aio
import time
import os
import uuid
import socket
import json
import sys

from tbsen_executor import TbsenExecutor
# from tbsen_parser import TbsenParser ## TODO: 추후 로직 수정 후 주석 제거

import filter_pb2
import filter_pb2_grpc


# ENV IP 체크
def get_host_ip(default_ip=""):
    if default_ip:
        print(f"[INFO] 환경변수 지정 IP 사용: {default_ip}")
        return default_ip

    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(("1.1.1.1", 80)) 
        detected_ip = s.getsockname()[0]
        s.close()
        print(f"[INFO] IP 자동감지 성공: {detected_ip}")
        return detected_ip
    except Exception as e:
        print(f"[WARN] IP 자동감지 실패: {e}. 'Unknown'으로 설정합니다.")
        return "Unknown"

# UUID 관리
def check_and_get_uuid(uuid_dir, uuid_file):
    if not os.path.exists(uuid_dir):
        try:
            os.makedirs(uuid_dir, exist_ok=True)
        except PermissionError:
            print(f"[CRITICAL] UUID 디렉터리 생성 권한 없음: {uuid_dir}")
            sys.exit(1)

    if not os.path.exists(uuid_file):
        new_uuid = str(uuid.uuid4())
        try:
            with open(uuid_file, "w") as f:
                f.write(new_uuid)
                print(f"[INIT] 새로운 UUID 발급 및 저장: {new_uuid}")
                return new_uuid
        except PermissionError:
            print(f"[CRITICAL] UUID 파일 쓰기 권한 없음: {uuid_file}")
            sys.exit(1)
    else:
        try:
            with open(uuid_file, "r") as f:
                existing_uuid = f.read().strip()
                if not existing_uuid:
                    raise ValueError("UUID 파일이 비어있음")
                print(f"[INFO] 기존 UUID 로드 완료: {existing_uuid}")
                return existing_uuid
        except Exception as e:
            print(f"[ERROR] UUID 로드 실패: {e}")
            sys.exit(1)

# 에이전트 등록
async def register_agent(stub, identity):
    while True:
        try:
            print(f"[INIT] 에이전트 등록 시도 {identity.uuid} ({identity.ip_address})")
            reg_resp = await stub.RegisterAgent(identity)
            print(f"[INFO] 에이전트 등록 성공: {reg_resp.message}")
            return True
        except grpc.RpcError as e:
            print(f"[WARN] 에이전트 등록 실패 (서버 다운): {e.details()} - 5초 후 재시도")
            await asyncio.sleep(5)
        except Exception as e:
            print(f"[ERROR] 등록 중 알 수 없는 오류: {e}")
            await asyncio.sleep(5)

# 명령 리스너
async def run_command_listener(stub, executor, identity):
    print(f"[START] 명령 리스너 시작 (UUID: {identity.uuid})")

    while True:
        await register_agent(stub, identity)

        print(f"[WAIT] 명령 수신 대기 중")
        try:
            # gRPC Stream 연결
            stream = stub.SubscribeCommands(identity)

            async for cmd in stream:
                payload_type = cmd.WhichOneof('payload')
                cid = cmd.command_id
                print(f"[RECV] CMD ID: {cid} | Type: {payload_type}")

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
                    msg = f"Exec Error: {str(e)}"
                    print(f"[FAIL] {msg}")

                # 결과 보고 (ACK)
                try:
                    report = filter_pb2.CommandResponse(command_id=cid, success=is_success, message=msg)
                    await stub.ReportCommandResult(report)
                    print(f"[REPORT] 결과 전송: {msg}")
                except grpc.RpcError as e:
                    print(f"[WARN] 결과보고 실패: {e}")

        except grpc.RpcError as e:
            print(f"[ERROR] gRPC 스트림 끊김: {e.code()} - 5초 후 재접속")
            await asyncio.sleep(5)
        except Exception as e:
            print(f"[CRITICAL] 리스너 루프 치명적 오류: {e}")
            await asyncio.sleep(5)

# 리포트 제너레이터
async def start_reporting(stub, executor, identity):
    print(f"[START] 상태 보고 서비스 시작")
    
    async def report_generator():
        while True:
            # TODO: 실제 bpftool 파싱 결과 연동
            # xdp_task = asyncio.to_thread(executor.get_xdp_status)
            # nft_task = asyncio.to_thread(executor.get_nft_ruleset)
            # raw_xdp_result, raw_nft_result = await asyncio.gather(xdp_task, nft_task)
            xdp_proto_details = [] 
            nft_json_str = json.dumps({"status": "maintenance", "msg": "Under Refactoring"})

            yield filter_pb2.AgentStatus(
                uuid=identity.uuid,
                timestamp=int(time.time()),
                hostname=identity.hostname,
                ip_address=identity.ip_address, 
                xdp_details=xdp_proto_details,
                xdp_mode=filter_pb2.XDP_MODE_NATIVE,
                nftables_raw_json=nft_json_str
            )
            await asyncio.sleep(10)
    while True:
        try:
            await stub.ReportStatus(report_generator())
        except grpc.RpcError as e:
            print(f"[WARN] 리포트 스트림 끊김: {e} - 재접속 대기")
            await asyncio.sleep(5)
        except Exception as e:
            print(f"[ERROR] 리포트 루프 오류: {e}")
            await asyncio.sleep(5)


# Main Entry Point
async def main(server_addr, host_ip, agent_uuid, hostname):
    executor = TbsenExecutor(use_sudo=True)
    
    identity = filter_pb2.AgentIdentity(
        uuid=agent_uuid,
        hostname=hostname,
        agent_version="0.2.0",
        ip_address=host_ip
    )

    print(f"========================================")
    print(f" tableSentinel Agent v0.2.0 ")
    print(f" - UUID: {agent_uuid}")
    print(f" - IP  : {host_ip}")
    print(f" - Host: {hostname}")
    print(f" - Target: {server_addr}")
    print(f"========================================")

    # gRPC 채널 옵션 (KeepAlive 설정)
    channel_options = [
        ('grpc.keepalive_time_ms', 10000),
        ('grpc.keepalive_timeout_ms', 10000),
        ('grpc.keepalive_permit_without_calls', 1),
        ('grpc.http2.max_pings_without_data', 0), 
        ('grpc.http2.min_time_between_pings_ms', 20000), 
        ('grpc.http2.min_ping_interval_without_data_ms', 20000)
    ]
    
    async with grpc.aio.insecure_channel(server_addr, options=channel_options) as channel:
        stub = filter_pb2_grpc.FilterAgentStub(channel)

        listener_task = asyncio.create_task(run_command_listener(stub, executor, identity))
        reporter_task = asyncio.create_task(start_reporting(stub, executor, identity))

        await asyncio.gather(listener_task, reporter_task)


if __name__ == "__main__":
    try:
        SERVER_ADDR = os.getenv("SERVER_ADDR", "localhost:8081") 
        UUID_DIR = os.getenv("UUID_DIR", "/etc/tbsen-agent")
        UUID_FILE = os.path.join(UUID_DIR, "agent-uuid")
        HOSTNAME = socket.gethostname()
        ENV_IP = os.getenv("TBSEN_AGENT_IP", "")

        final_ip = get_host_ip(ENV_IP)
        final_uuid = check_and_get_uuid(UUID_DIR, UUID_FILE)

        asyncio.run(main(SERVER_ADDR, final_ip, final_uuid, HOSTNAME))

    except KeyboardInterrupt:
        print("\n[SHUTDOWN] 에이전트 종료")