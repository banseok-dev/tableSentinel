package com.tbsen.controller;

import com.tbsen.dto.AgentIdentityDto;
import com.tbsen.dto.NftCommandDto;
import com.tbsen.dto.XdpCommandDto;
import com.tbsen.service.FilterAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
public class AgentController {

    private final FilterAgentService filterAgentService;

    // 연결된 에이전트 목록 조회
    @GetMapping
    public ResponseEntity<List<AgentIdentityDto>> getConnectedAgents() {
        return ResponseEntity.ok(filterAgentService.getConnectedAgents());
    }

    // XDP 명령 전송
    @PostMapping("/{uuid}/xdp")
    public ResponseEntity<String> sendXdpCommand(@PathVariable("uuid") String uuid, @RequestBody XdpCommandDto dto) {
        log.info("[API] XDP Command 요청: Agent={}, IP={}", uuid, dto.getTargetIp());
        boolean sent = filterAgentService.executeXdpCommand(uuid, dto);
        
        if (sent) return ResponseEntity.ok("Command Sent");
        else return ResponseEntity.status(503).body("Agent Not Connected or Failed");
    }

    // NFTables 명령 전송
    @PostMapping("/{uuid}/nft")
    public ResponseEntity<String> sendNftCommand(@PathVariable("uuid") String uuid, @RequestBody NftCommandDto dto) {
        log.info("[API] NFT Command 요청: Agent={}, IP={}", uuid, dto.getTargetIp());
        boolean sent = filterAgentService.executeNftCommand(uuid, dto);
        
        if (sent) return ResponseEntity.ok("Command Sent");
        else return ResponseEntity.status(503).body("Agent Not Connected or Failed");
    }
}