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
    # nftables API
    # ----------------------------------------

    # [status] read rule
    def execute_nft_ruleset(self) -> Tuple[bool, Any]:
        command = ["nft", "-j", "list", "ruleset"]
        return self._execute_command(command, is_json_output=True)
    
    def execute_nft_blocked_ip(self, ip_address: str) -> Tuple[bool, Any]:
        command = [
            "nft", "add", "rule", 
            "inet", "filter", "input", 
            "ip", "saddr", ip_address, 
            "drop"
        ]
        return self._execute_command(command)

    # ----------------------------------------
    # XDP API
    # ----------------------------------------

    # [status] read rule
    def get_xdp_status(self) -> Tuple[bool, Any]:
        command = ["xdp-filter", "status"]
        return self._execute_command(command)

    # [ip] add ip rule
    def add_xdp_ip_rule(self, ip_address: str, mode: str = "src,dst") -> Tuple[bool, Any]:
        command = ["xdp-filter", "ip", "-m", mode, ip_address]
        return self._execute_command(command)

    # [ip] delete ip rule
    def delete_xdp_ip_rule(self, ip_address: str, mode: str = "src,dst") -> Tuple[bool, Any]:
        command = ["xdp-filter", "ip", "-r", "-m", mode, ip_address,]
        return self._execute_command(command)
    
    # [mac] add mac rule
    def add_xdp_mac_rule(self, mac_address: str, mode: str = "src,dst") -> Tuple[bool, Any]:
        command = ["xdp-filter", "ether", "-m", mode, mac_address,]
        return self._execute_command(command)

    # [mac] delete mac rule
    def delete_xdp_mac_rule(self, mac_address: str, mode: str = "src,dst") -> Tuple[bool, Any]:
        command = ["xdp-filter", "ether", "-r", "-m", mode, mac_address,]
        return self._execute_command(command)