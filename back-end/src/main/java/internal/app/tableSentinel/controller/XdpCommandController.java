package internal.app.tableSentinel.controller;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import internal.app.tableSentinel.dto.XdpCommandDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/agents/{agentId}/xdp")
public class XdpCommandController {
    private final Map<String, List<XdpCommandDto>> commandQueue = new ConcurrentHashMap<>();

    // Front-end to Back-end : (POST) 관리자 명령 (웹 GUI)
    @PostMapping("/commands")
    public String sendCommand(
        @PathVariable("agentId") String agentId,
        @RequestBody XdpCommandDto commandDto
    ) {
        commandDto.setTimestamp(System.currentTimeMillis());
        commandQueue.putIfAbsent(agentId, new ArrayList<>());
        commandQueue.get(agentId).add(commandDto);

        System.out.println("[Frontend -> Backend] 명령 수신:" + agentId + " / " + commandDto.getType() + " " + commandDto.getIpAddress());

        return "Command Queued Successfully";
    }

    // Agents Polling commands 
    @GetMapping("/commands/poll")
    public List<XdpCommandDto> pollCommands(
        @PathVariable("agentId") String agentId
    ) {

        List<XdpCommandDto> commands = commandQueue.get(agentId);

        if (commands != null && !commands.isEmpty()) {
            List<XdpCommandDto> toSend = new ArrayList<>(commands);

            commands.clear();

            System.out.println("백엔드 -> 에이전트 명령전달:" + toSend.size() + "건");
            return toSend;
        }
        return List.of();

    }

    @PostMapping("/reports")
    public String receiveReport(
        @PathVariable("agentId") String agentId,
        @RequestBody Map<String, Object> reportData // 일단 Map으로 받아서 로그만 찍음
    ) {
        System.out.println("📊 [Report 수신] From: " + agentId);
        System.out.println("   내용: " + reportData);
        
        // 추후 프론트 엔드와 연결 필요(DB 연결은 하지말것)
        return "Report Received";
    }
}
