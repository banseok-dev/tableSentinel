# TableSentinel Parser 프로그램입니다.
# 해당 프로그램의 목표는 각 Tool의 테이블을 불러오고, 모니터링 요약을 목적으로 가지고 있습니다.
# 파이프라인 구성중입니다.

import tbsen_executor
import re
from typing import Tuple, Any

class TbsenParser:

    @staticmethod
    def parse_nftables_detail(nftables_data: str) -> Tuple[bool, Any]:
        parser_data = nftables_data.strip('\n')
        print(parser_data)
        return True, parser_data
        pass

    @staticmethod
    def parse_xdp_status(xdp_raw_text: str) -> Tuple[bool, Any]:
        parsed_data = {
            "stats": {
                "aborted": {"pkts": 0, "kib": 0},
                "drop": {"pkts": 0, "kib": 0},
                "pass": {"pkts": 0, "kib": 0}
            },
            "interfaces": [],
            "ports": [],
            "ips": [],
            "macs": []
        }

        current_section = None

        patterns = {
            "stats": re.compile(r"^\s+XDP_(\w+)\s+(\d+)\s+pkts\s+(\d+)\s+KiB$"),
            "ifaces": re.compile(r"^\s+([\w\.-]+)\s+\(([\w\s]+)\)\s+([\w,]+)$"),
            "ips": re.compile(r"^\s+([\d\.]+)\s+([\w,]+)\s+(\d+)$")
        }

        for line in xdp_raw_text.strip().split('\n'):
            line_stripped = line.strip()
            if not line_stripped:
                continue

            if line_stripped.startswith("Aggregate per-action statistics:"):
                current_section = "stats"
                continue
            elif line_stripped.startswith("Loaded on interfaces:"):
                current_section = "ifaces"
                continue
            elif line_stripped.startswith("Filtered ports:"):
                current_section = "ports"
                continue
            elif line_stripped.startswith("Filtered IP addresses:"):
                current_section = "ips"
                continue
            elif line_stripped.startswith("Filtered MAC addresses:"):
                current_section = "macs"
                continue
            
            # [macth section] 파싱 부분별 match
            match current_section:
                case "stats":
                    match = re.search(patterns["stats"], line)
                    if match:
                        action, pkts, kib = match.groups()
                        action_key = action.lower()
                        if action_key in parsed_data["stats"]:
                            parsed_data["stats"][action_key] = {
                                "pkts": int(pkts),
                                "kib": int(kib)
                            }
                case "ifaces":
                    match = re.search(patterns["ifaces"], line)
                    if match:
                        if_name, if_mode, features_str = match.groups()
                        parsed_data["interfaces"].append({
                            "name": if_name,
                            "mode": if_mode,
                            "features": features_str.split(',')
                        })
                case "ips":
                    match = re.search(patterns["ips"], line)
                    if match:
                        ip, mode_str, hits = match.groups()
                        parsed_data["ips"].append({
                            "value": ip,
                            "mode": mode_str.split(','),
                            "hits": int(hits)
                                })     
                # (TODO: 'ports', 'macs' 케이스 추가)
                
                case _:
                    # "stats"도, "ifaces"도 아닌 '헤더'나 '빈 줄'은
                    # '기본값(default)' 케이스에 걸려 '무시'됨
                    pass

        return True, parsed_data

# 테스트 코드로 분리 필요함(메인함수 그만쓰자)
if __name__ == "__main__":
    print("tableSentinel 에이전트 테스트")

    executor = tbsen_executor.TbsenExecutor(use_sudo=True)
    print("테스트")
    success, data = executor.get_xdp_status()
    if success:
        print(f"성공 (텍스트 수신): {data}")
    else:
        print(f"실패: {data}")

    success, rule = TbsenParser.parse_xdp_status(data)
    if success:
        print(f"출력성공: {rule}")
    else:
        print(f"출력실패: {rule}")