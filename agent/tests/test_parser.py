import unittest
import sys
import os

sys.path.append(os.path.dirname(os.path.abspath(os.path.dirname(__file__))))

from tbsen_parser import TbsenParser

class TestTbsenParser(unittest.TestCase):
    
    MOCK_XDP_STATUS = r"""CURRENT XDP-FILTER STATUS:

    Aggregate per-action statistics:
    XDP_ABORTED                                   1 pkts           4 KiB
    XDP_DROP                                    259 pkts          20 KiB
    XDP_PASS                                    300 pkts        6016 KiB

    Loaded on interfaces:
                                            Enabled features
    xdpfilt_alw_all
    enp0 (skb mode)                      tcp,udp,ipv6,ipv4,ethernet,allow
    xdpfilt_alw_all
    enp1 (native mode)                   tcp,udp,ipv6,ipv4,ethernet,allow

    Filtered ports:
                                            Mode             Hit counter

    Filtered IP addresses:
                                            Mode             Hit counter

    Filtered MAC addresses:
                                            Mode             Hit counter
    """

    def test_parse_xdp_status_success(self):
        success, data = TbsenParser.parse_xdp_status(self.MOCK_XDP_STATUS)
        
        self.assertTrue(success)
        self.assertEqual(data['interfaces'][0]['name'], 'enp1')
        
        print("\n✅ XDP 파싱 테스트 통과")

if __name__ == '__main__':
    unittest.main()