import re
from typing import Tuple, Any

class TbsenParser:

    @staticmethod
    def parse_nftables_detail(nftables_data: str) -> Tuple[bool, Any]:
        parser_data = nftables_data.strip('\n')
        print(parser_data)
        return True, parser_data


    # eBPF 맵 직접 분석 필요
    @staticmethod
    def parse_xdp_status(xdp_raw_text: str) -> Tuple[bool, Any]:
        parsed_data = {
            "stats": {
                "aborted": {"pkts": 0, "kib": 0},
                "drop": {"pkts": 0, "kib": 0},
                "pass": {"pkts": 0, "kib": 0}
            },
            "interfaces": [],
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
            # TODO: 파싱 요소 추가필요
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

                case _:
                    pass

        return True, parsed_data