package internal.app.tableSentinel.controller;

import internal.app.tableSentinel.dto.ReportsDto;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ReportsController {
    @ResponseBody
    @PostMapping("/api/agents/{agentId}/reports")
    public String receiveAgentReport(
        @PathVariable("agentId") String agentId
    ) {
        System.out.println("보고서 수신 (에이전트 ID): " + agentId);
        return "Report received for " + agentId;
    }
}
