package com.tbsen.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    @JsonProperty("agent_version")
    private String agent_version;
    @JsonProperty("ip_address")
    private String ip_address;
    @JsonProperty("last_heartbeat")
    private String last_heartbeat;
    private String status;
}