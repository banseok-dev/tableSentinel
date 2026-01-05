from typing import Tuple, Any
from tbsen_executor import TbsenExecutor

# 파서 로직 준비중 -> 모니터리 연계 필요
class TbsenParser:

    # TODO: 데이터 가공 로직 필요
    @staticmethod
    def parse_nftables_status() -> Tuple[bool, Any]:
        parser_data = TbsenExecutor().get_nft_ruleset()
        print(parser_data)
        return True, parser_data


    # TODO: 데이터 가공 로직 필요 
    @staticmethod
    def parse_xdp_status() -> Tuple[bool, Any]:
        # 임시 변수 추후 재가공
        map_name = ["xdp_stats_map",
                    "filter_ports",
                    "filter_ipv4",
                    "filter_ipv6",
                    "filter_ethernet"]
        parsed_data = TbsenExecutor().get_bpf_dump("xdp_stats_map")
        print(parsed_data)
        return True, parsed_data