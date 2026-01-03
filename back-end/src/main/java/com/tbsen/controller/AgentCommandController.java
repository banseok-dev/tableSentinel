package com.tbsen.controller;

import com.tbsen.dto.XdpCommandDto;
import com.tbsen.dto.NftCommandDto;
import com.tbsen.service.FilterAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/agents/{agentId}")
@RequiredArgsConstructor
public class AgentCommandController {

    private final FilterAgentService filterAgentService;

    // XDP Commnads 처리 (Front <-> Back)
    @PostMapping("/xdp/commands")
    public ResponseEntity<String> sendXdpCommand(@PathVariable("agentId") String agentId, @RequestBody XdpCommandDto dto) {
        log.info("[INFO] XDP 요청 수신: Agent={}, Action={}", agentId, dto.getAction());

        boolean success = filterAgentService.executeXdpCommand(agentId, dto);

        if (success) {
            return ResponseEntity.ok("[SUCCESS] XDP Command 전송 성공");
        } else {
            return ResponseEntity.status(503).body("[FAILED] XDP Command 전송 실패: 에이전트 확인 필요");
        }
    }

    // NFT Commands 처리 (Front <-> Back)
    @PostMapping("/nft/commands")
    public ResponseEntity<String> sendNftCommand(@PathVariable("agentId") String agentId, @RequestBody NftCommandDto dto) {
        log.info("[INFO] NFT 요청 수신: Agent={}, Action={}", agentId, dto.getAction());

        boolean success = filterAgentService.executeNftCommand(agentId, dto);

        if (success) {
            return ResponseEntity.ok("[SUCCESS] NFT Command 전송 성공");
        } else {
            return ResponseEntity.status(503).body("[FAILED] NFT Command 전송 실패: 에이전트 확인 필요");
        }
    }
}