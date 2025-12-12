import subprocess
import json
from typing import Tuple, Any

class TbsenExecutor:
    def __init__(self, use_sudo: bool = True):
        self.command_prefix = ["sudo"] if use_sudo else []

    def _execute_command(self, command: list[str], is_json_output: bool = False) -> Tuple[bool, Any]:
        full_command = self.command_prefix + command

        try:
            result = subprocess.run(
            full_command,
            check=True,
            capture_output=True,
            text=True,
            encoding='utf-8'
            )

            output = result.stdout.strip()

            if is_json_output:
                try:
                    return True, json.loads(output)
                except json.JSONDecodeError:
                    return False, f"JSON 파싱 실패: {output}"
            return True, output
        
        except subprocess.CalledProcessError as e:
            return False, f"명령 실행 실패: {e.stderr.strip()}"
        except  FileNotFoundError:
            return False, f"명령어 '{command[0]}'를 찾을 수 없습니다."

    # ----------------------------------------
    # nftables API - filter only
    # ----------------------------------------

    # [status] read rule
    def get_nft_ruleset(self) -> Tuple[bool, Any]:
        command = ["nft", "-j", "list", "ruleset"]
        return self._execute_command(command, is_json_output=True)
    
    # [all] delete rule
    def del_nft_rule(self, chain: str, rule_number: str) -> Tuple[bool, Any]:
        command = ["nft", "delete", "rule", "inet", "filter", chain, "handle", rule_number]
        return self._execute_command(command)
    
    # [ip] add ip accept
    def add_nft_allow_ip(self, chain: str, ip_address: str) -> Tuple[bool, Any]:
        command = ["nft", "add", "rule", "inet", "filter", chain, "ip", "saddr", ip_address, "accept"]
        return self._execute_command(command)

    # [ip] add ip drop
    def add_nft_drop_ip(self, chain: str, ip_address: str) -> Tuple[bool, Any]:
        command = ["nft", "add", "rule", "inet", "filter", chain, "ip", "saddr", ip_address, "drop"]
        return self._execute_command(command)
    
    # [ip] add TCP/UDP accept
    def add_nft_accept_l4_protocol(self, chain: str, l4_protocol: str, port:str, ip_address: str) -> Tuple[bool, Any]:
        command = ["nft", "add", "rule", "inet", "filter", chain, l4_protocol, "dport", port, "ip", "saddr", ip_address, "accept"]
        return self._execute_command(command)

    # [ip] add TCP/UDP drop
    def add_nft_drop_l4_protocol(self, chain: str, l4_protocol: str, port:str, ip_address: str) -> Tuple[bool, Any]:
        command = ["nft", "add", "rule", "inet", "filter", chain, l4_protocol, "dport", port, "ip", "saddr", ip_address, "drop"]
        return self._execute_command(command)


    # ----------------------------------------
    # XDP API
    # ----------------------------------------

    # 인터페이스는 XDP Filter에서 먼저 선택되어 처리되므로, nftables 인터페이스 선택기능은 빼기

    # [status] read rule
    def get_xdp_status(self) -> Tuple[bool, Any]:
        command = ["xdp-filter", "status"]
        return self._execute_command(command)
    
    # [interface] 인터페이스 로드
    def load_xdp_ineterface(self, interface: str) -> Tuple[bool, Any]:
        # 로드 실패시 명령어 반환 되므로 기본 값으로 설정 -> 기본이 native이므로,
        # native가 아닐 경우 Couldn't attach XDP program on iface '인터페이스 이름': Operation not supported(-95)
        # 에러를 띄우므로 이걸 반환하도록 하면됨.
        command = ["xdp-filter", "load", interface]
        return self._execute_command(command)

    # [interface] interface 호환성 모드로 로드 -> 프론트에서 체크옵션으로 처리하면 될듯
    def load_xdp_interface_skb(self, interface: str) -> Tuple[bool, Any]:
        command = ["xdp-filter", "load", interface, "-m", "skb"]
        return self._execute_command(command)

    # [interface] 인터페이스 언로드
    def unload_force_xdp_interface(self, interface: str) -> Tuple[bool, Any]:
        command = ["xdp-filter", "unload", interface]
        return self._execute_command(command)

    # 프론트/백엔드에서 src, dst 모드 추가해줘야할듯.
    # [ip] 규칙 IP주소 추가
    # mode = direction (fe,be 데이터와 연결되어있음)
    def add_xdp_ip_rule(self, ip_address: str, mode: str) -> Tuple[bool, Any]:
        command = ["xdp-filter", "ip", "-m", mode, ip_address]
        return self._execute_command(command)

    # [ip] 규칙 IP주소 제거
    def delete_xdp_ip_rule(self, ip_address: str, mode: str) -> Tuple[bool, Any]:
        command = ["xdp-filter", "ip", "-r", "-m", mode, ip_address,]
        return self._execute_command(command)
    
    # [mac] 규칙 MAC주소 추가
    def add_xdp_mac_rule(self, mac_address: str, mode: str) -> Tuple[bool, Any]:
        command = ["xdp-filter", "ether", "-m", mode, mac_address,]
        return self._execute_command(command)

    # [mac] 규칙 MAC주소 제거
    def delete_xdp_mac_rule(self, mac_address: str, mode: str) -> Tuple[bool, Any]:
        command = ["xdp-filter", "ether", "-r", "-m", mode, mac_address,]
        return self._execute_command(command)