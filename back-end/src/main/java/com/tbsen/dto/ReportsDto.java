package com.tbsen.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportsDto {
    private String uuid;
    private String timestamp;
    
    // 에이전트 식별 정보
    private String hostname;
    private String ipAddress;

    // 요약 메트릭 (대시보드 차트용)
    private long totalDropCount;
    private long totalPassCount;
    
    // 상세 데이터
    private String xdpMode;         // "NATIVE" or "SKB"
    private String nftablesJson;    // nft JSON
    
    // 추후 DB 추가시 위 변수들이 컬럼이 될 예정
}