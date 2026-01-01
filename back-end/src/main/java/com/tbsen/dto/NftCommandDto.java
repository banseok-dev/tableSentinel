package com.tbsen.dto;

import lombok.Data;

@Data
public class NftCommandDto {
    private String action;
    private String chain;    // [nft 단독] INPUT, OUTPUT
    private String targetIp;
    private String protocol; // [nft 단독] tcp, udp
    private String port;     // [nft 단독] port
    private String handle;   // [nft 단독] 룰 번호
}