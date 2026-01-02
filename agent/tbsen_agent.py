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
SERVER_ADDR = "192.168.0.11:8081"


# Host Identity
UUID_DIR = "/etc/tbsen-agent"
HOST_UUID_FILE = f"{UUID_DIR}/agent-uuid"
HOSTNAME = socket.gethostname()

if not os.path.exists(UUID_DIR):
    try:
        os.makedirs(UUID_DIR, exist_ok=True)
    except PermissionError:
        print(f"[ERROR] UUID: {UUID_DIR} 디렉터리를 생성할 권한이 없습니다.")
        print(f"[SHUTDOWN] 에이전트를 종료합니다.")
        exit(1)

if not os.path.exists(HOST_UUID_FILE):
    agent_uuid = str(uuid.uuid4())
    try:
        with open(HOST_UUID_FILE, "w") as f:
            f.write(agent_uuid)
            print(f"[INFO] UUID: {agent_uuid} 파일 생성 완료")
    except PermissionError:
        print(f"[ERROR] UUID: 파일 쓰기 권한이 없습니다.")
        print(f"[SHUTDOWN] 에이전트를 종료합니다.")
        exit(1)
else:
    with open(HOST_UUID_FILE, "r") as f:
        agent_uuid = f.read().strip()
        print(f"[INFO] UUID: {agent_uuid} 파일 읽기 성공")



# Command Logic
async def run_command_listener(stub, executor):
    identity = filter_pb2.AgentIdentity(
        uuid=agent_uuid, 
        hostname=HOSTNAME,
        agent_version="0.2.0"
    )
    print(f"[START] tableSentinel-Agent: {HOSTNAME} 명령 서비스 시작")

    try:
        stream = stub.SubscribeCommands(identity)
        async for cmd in stream:
            payload_type = cmd.WhichOneof('payload')
            print(f"[INFO] COMMAND ID: {cmd.command_id} | Type: {payload_type}")

            match payload_type:
                # XDP Logic (L2)
                case "xdp":
                    xdp = cmd.xdp
                    mode = xdp.mode if xdp.mode else "src,dst" # 기본값 처리

                    # [비동기 래핑] 동기 함수를 스레드로 분리하여 Event Loop 멈춤 방지
                    # IP 제어
                    if xdp.target_ip:
                        match xdp.action:
                            case filter_pb2.ACTION_ADD:

                                await asyncio.to_thread(executor.add_xdp_ip_rule, xdp.target_ip, mode)
                                print(f"[RESULT] XDP IP 차단: {xdp.target_ip} ({mode})")
                            
                            case filter_pb2.ACTION_DELETE:
                                await asyncio.to_thread(executor.delete_xdp_ip_rule, xdp.target_ip, mode)
                                print(f"[RESULT] XDP IP 차단 해제: {xdp.target_ip} ({mode})")
                    # MAC 제어
                    elif xdp.target_mac:
                        match xdp.action:
                            case filter_pb2.ACTION_ADD:
                                await asyncio.to_thread(executor.add_xdp_mac_rule, xdp.target_mac, mode)
                                print(f"[RESULT] XDP MAC 차단: {xdp.target_mac} ({mode})")
                            case filter_pb2.ACTION_DELETE:
                                await asyncio.to_thread(executor.delete_xdp_mac_rule, xdp.target_mac, mode)
                                print(f"[RESULT] XDP MAC 차단 해제: {xdp.target_mac} ({mode})")

                    # 인터페이스 제어
                    elif xdp.interface_name:
                        match xdp.action:
                            case filter_pb2.ACTION_ADD:
                                if xdp.interface_mode == filter_pb2.XDP_MODE_SKB:
                                    await asyncio.to_thread(executor.load_xdp_interface_skb, xdp.interface_name)
                                    print(f"[RESULT] XDP 로드 (SKB): {xdp.interface_name}")
                                else:
                                    await asyncio.to_thread(executor.load_xdp_interface, xdp.interface_name) 
                                    print(f"[RESULT] XDP 로드 (Native): {xdp.interface_name}")
                            
                            case filter_pb2.ACTION_DELETE:
                                await asyncio.to_thread(executor.unload_force_xdp_interface, xdp.interface_name)
                                print(f"[RESULT] XDP 언로드: {xdp.interface_name}")

                # NFTables Logic (L3/L4) -> 추가 기능 구현 필요(현재 IP DROP만 구현)
                case "nft":
                    nft = cmd.nft
                    
                    match nft.action:
                        case filter_pb2.ACTION_ADD:
                            if nft.target_ip and nft.chain:
                                if nft.protocol and nft.port:
                                    # specific port DROP
                                    await asyncio.to_thread(
                                        executor.add_nft_drop_l4_protocol,
                                        nft.chain, nft.protocol, nft.port, nft.target_ip
                                    )
                                    print(f"[SUCCESS] 정밀 차단({nft.protocol}/{nft.port}): {nft.target_ip}")
                                else:
                                    # IP DROP
                                    await asyncio.to_thread(executor.add_nft_drop_ip,nft.chain, nft.target_ip)
                                    print(f"[SUCCESS] 전면 차단(All Traffic): {nft.target_ip}")
                            else:
                                print(f"[ERROR] 규칙 추가 실패: IP 또는 Chain 누락")

                        case filter_pb2.ACTION_DELETE:
                            if nft.chain and nft.handle:
                                await asyncio.to_thread(executor.del_nft_rule, nft.chain, nft.handle)
                                print(f"[SUCCESS] 규칙 삭제(Handle): {nft.handle}")
                            else:
                                print(f"[ERROR] 규칙 삭제 실패: Handle 누락")

                case _:
                    print(f"[ERROR] 알 수 없는 Payload: {payload_type}")

    except grpc.RpcError as e:
        print(f"[ERROR] gRPC 연결 끊김: {e}, 5초 대기")
        await asyncio.sleep(5)


# Report Generator
async def run_status_reporter(stub, executor):
    print(f"[START] tableSentinel-Agent: 리포트 서비스 시작")
    while True:
        try:
            success, raw_data = await asyncio.to_thread(executor.get_xdp_status)
            
            if success:
                p_success, clean_data = TbsenParser.parse_xdp_status(raw_data)
                
                if p_success:
                    status_msg = filter_pb2.AgentStatus(
                        uuid=agent_uuid,
                        timestamp=int(time.time()),
                    )
                    
                    print(f"[INFO] 상태 보고 전송 완료 (Timestamp: {status_msg.timestamp})")
            
        except Exception as e:
            print(f"[ERROR] 상태 보고 전송 실패: {e}")
            
        await asyncio.sleep(10)

# Main Entry Point
async def main():
    executor = TbsenExecutor(use_sudo=True)
    
    async with grpc.aio.insecure_channel(SERVER_ADDR) as channel:
        stub = filter_pb2_grpc.FilterAgentStub(channel)
        
        # Agent 서버 등록
        try:
            reg_resp = await stub.RegisterAgent(filter_pb2.AgentIdentity(uuid=agent_uuid))
            print(f"[INFO] 에이전트 서버 등록 성공: {reg_resp.message}")
        except grpc.RpcError as e:
            print(f"[INFO] 에이전트 서버 등록 실패 (서버 연결 확인): {e}")
            return

        # 비동기 병렬 실행
        await asyncio.gather(
            run_command_listener(stub, executor),
            run_status_reporter(stub, executor)
        )

if __name__ == "__main__":
    try:
        asyncio.run(main())
    except KeyboardInterrupt:
        print("[GRACEFUL_SHUTDOWN] 에이전트 사용자 요청종료")