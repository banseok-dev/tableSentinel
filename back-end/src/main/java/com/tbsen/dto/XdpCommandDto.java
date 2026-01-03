package com.tbsen.dto;
import lombok.Data;

@Data
public class XdpCommandDto {
    private String action;
    private String targetIp;
    private String targetMac;     // [XDP 단독] MAC 주소
    private String mode;          // [XDP 단독] src, dst
    private String interfaceName; // [XDP 단독] interface name
    private String interfaceMode; // [XDP 단독] skb, native 등
}