package com.tbsen.dto;
import lombok.Data;

@Data
public class ReportsDto {
    // 데이터 모델링 후 개선 필요
    private String agentId;
    private String commandType;
    private long timestamp;
    private String family;
    private String table;
    private String chain;
    private String name;
    private String ipAddress;
}
