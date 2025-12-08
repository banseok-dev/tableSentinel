import time
import requests
import json
import os
from tbsen_executor import TbsenExecutor
from tbsen_parser import TbsenParser

# 에이전트 호스트 uuid 설정
import uuid

'''
## UUID Setup
HOST_UUID_FILE = "/etc/tbsen-agent/agent-uuid"

if not os.path.exists(HOST_UUID_FILE):
    agent_uuid = str(uuid.uuid4())
    with open(HOST_UUID_FILE, "w") as f:
        f.write(agent_uuid)
else:
    with open(HOST_UUID_FILE, "r") as f:
        agent_uuid = f.read().strip()

print(f"Agent ID: {agent_uuid}")
'''

# 설정 (나중에는 환경변수로 분리)
BACKEND_URL = "http://192.168.0.11:8080/api/agents" # <- 추후 백엔드 URL 수정할 수 있도록 해야함
AGENT_ID = 'node1'

def main():
    print(f"    🛎️ tableSentinel Agent ({AGENT_ID}) Started")
    
    # 인스턴스 생성 (sudo 강제 on)
    executor = TbsenExecutor(use_sudo=True)

    # Parser는 @staticmethod라서 인스턴스 생성 불필요

    while True:
        try:
            print("\n[Loop] 작업 시작...")

            # -------------------------------------------------
            # get agent command
            # -------------------------------------------------
            poll_url = f"{BACKEND_URL}/{AGENT_ID}/commands/poll"
            try:
                response = requests.get(poll_url, timeout=5)
                if response.status_code == 200:
                    commands = response.json()
                    if commands:
                        print(f"[명령 수신] {len(commands)}개의 명령을 처리합니다.")
                        for cmd in commands:
                            print(f"  -> 명령 실행: {cmd}")
                            engineType = cmd.get('engineType')
                            cmd_type = cmd.get('commandType')
                            target_ip = cmd.get('ipAddress')
                            taget_chain = cmd.get('targetChain')
                            
                            # XDP 액션 수행
                            match engineType:
                                case "XDP":
                                    match cmd_type:
                                        case "ADD_IP":
                                            print(f" [Action] IP 차단 실행: {target_ip}")
                                            success, result = executor.add_xdp_ip_rule(target_ip)
                                            if success:
                                                print(" O 성공 ")
                                            else:
                                                print(f" X 실패: {result}")
                                        case "DEL_IP":
                                            print(f" [Action] IP 차단 해제: {target_ip}")
                                            success, result = executor.delete_xdp_ip_rule(target_ip)
                                            if success:
                                                print(" O 성공 ")
                                            else:
                                                print(f" X 실패: {result}")
                                        case _:
                                            pass

                                # nftables 액션
                                case "nftables":
                                    match cmd_type:
                                        case "ADD_IP":
                                            success, result = executor.add_nft_allow_ip(taget_chain, target_ip)
                                            print(f" nftables [Action] IP 허용 실행: {taget_chain, target_ip}")
                                        case "DEL_IP":
                                            success, result = executor.add_nft_drop_ip(taget_chain, target_ip)
                                            print(f" nftables [Action] IP 거부 실행: {taget_chain, target_ip}")
                                        case _:
                                            pass
                else:
                    print(f"[Polling 실패] 상태 코드: {response.status_code}, 상태: {response.ok}")
            except Exception as e:
                print(f"[통신 에러] 백엔드 연결 불가: {e}")
            time.sleep(5)
            ''' 상태보고 OFF
            # -------------------------------------------------
            # 상태 보고 (Reporting) - (일단 XDP 상태만 보고 불러옮)
            # -------------------------------------------------
            # Executor로 raw data 가져오기
            success, raw_data = executor.get_xdp_status()

            if success:
                p_success, clean_data = TbsenParser.parse_xdp_status(raw_data)
                if p_success:
                    # 깔끔하게 정제된 데이터 출력 (나중에는 백엔드로 전송)
                    print(f"[상태 조회 성공] 데이터: {json.dumps(clean_data, indent=2)}")
                else:
                    print(f"[파싱 실패] 날것 데이터: {raw_data}")

            p_success, clean_data = TbsenParser.parse_xdp_status(raw_data)

            if p_success:
                print(f"[상태 조회 성공] 데이터 파싱 완료")
                
                # 1. 최종 보고서(Report) 조립 (API 규격 맞추기)
                # (나중에 hostname이나 timestamp도 여기에 추가)
                final_report = {
                    "agentId": AGENT_ID,
                    "status": clean_data  # 파싱된 XDP 데이터
                }

                # 백엔드 패킷 전송 (POST)
                report_url = f"{BACKEND_URL}/{AGENT_ID}/reports"
                try:
                    # json=final_report를 쓰면 requests가 알아서 json.dumps + 헤더 설정을 해줌
                    res = requests.post(report_url, json=final_report, timeout=5)
                    if res.status_code == 200:
                        print("    🚀 [Report] 백엔드 통신(전송) 성공")
                    else:
                        print(f"    ⚠️ [Report] 백엔드 통신(전송) 실패: {res.status_code} - {res.text}")
                except Exception as e:
                     print(f"    ❌ [Report] 통신 에러: {e}")
            # TODO: 이걸로 괜찮은건지? Rust로 전환시 호스트 방화벽을 어떻게 이슈처리할지 고민.
            # -> Polling 방식은 클라이언트 단에서 OUTPUT 되는 패킷이므로, establish 될 경우 안정적인 통신 가능
            # -> 그런데 호스트 자원을 꽤 먹을것 같아서 좀 걱정됨
            # 반복 대기(10s, CPU 부하 줄임)
            time.sleep(10)
            '''
        # 예외처리
        except KeyboardInterrupt:
            print("에이전트를 종료합니다.")
            break
        except Exception as e:
            print(f"치명적 에러 발생 [에러 메시지]: {e}")
            time.sleep(5)

if __name__ == "__main__":
    main()