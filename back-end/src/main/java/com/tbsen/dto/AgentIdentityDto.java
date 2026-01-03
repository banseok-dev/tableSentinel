package com.tbsen.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 에이전트 -> 백엔드
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentIdentityDto {
    private String uuid;
    private String hostname;
    private String agent_version;
    private String ip_address;
    private String last_heartbeat;
    private String status;
}