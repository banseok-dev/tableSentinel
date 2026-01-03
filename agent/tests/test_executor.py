import unittest
import sys
import os
from unittest.mock import patch, MagicMock

sys.path.append(os.path.dirname(os.path.abspath(os.path.dirname(__file__))))

from tbsen_executor import TbsenExecutor

class TestTbsenExecutor(unittest.TestCase):

    def setUp(self):
        self.executor = TbsenExecutor(use_sudo=False)

    @patch('subprocess.run')
    def test_add_nft_drop_ip(self, mock_run):
        
        mock_result = MagicMock()
        mock_result.returncode = 0
        mock_result.stdout = ""
        mock_run.return_value = mock_result

        success = self.executor.add_nft_drop_ip("input", "1.2.3.4")

        self.assertTrue(success)
        
        expected_cmd = ["nft", "add", "rule", "inet", "filter", "input", "ip", "saddr", "1.2.3.4", "drop"]
        
        args, _ = mock_run.call_args
        self.assertEqual(args[0], expected_cmd)
        
        print("\n[SUCCESS] Executor 명령어 조립 테스트 통과")

if __name__ == '__main__':
    unittest.main()