package com.tbsen.controller;

import com.tbsen.dto.BaseCommandDto;
import com.tbsen.dto.NftCommandDto;
import com.tbsen.dto.XdpCommandDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/agents/{agentId}")
@RequiredArgsConstructor
public class AgentCommandController {

    private final Map<String, List<BaseCommandDto>> commandQueue = new ConcurrentHashMap<>();

    // XDP Commands 처리부
    @PostMapping("/xdp/commands")
    public String addXdpCommand(
            @PathVariable("agentId") String agentId,
            @RequestBody XdpCommandDto dto 
    ) {
        dto.setAgentId(agentId);
        dto.setTimestamp(System.currentTimeMillis());

        addCommandToQueue(agentId, dto);

        return "XDP Command Queued";
    }

    @PostMapping("/nft/commands")
    public String addNftCommand(
            @PathVariable("agentId") String agentId,
            @RequestBody NftCommandDto dto
    ) {
        dto.setAgentId(agentId);
        dto.setTimestamp(System.currentTimeMillis());

        addCommandToQueue(agentId, dto);

        return "NFT Command Queued";
    }

    private void addCommandToQueue(String agentId, BaseCommandDto dto) {
        commandQueue.computeIfAbsent(agentId, k -> new ArrayList<>()).add(dto);
        System.out.println("[Queue] Command added for " + agentId + ": " + dto);
    }
}