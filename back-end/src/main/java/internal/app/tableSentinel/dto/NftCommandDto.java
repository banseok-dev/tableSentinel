package internal.app.tableSentinel.dto;

import lombok.Data;

@Data
public class NftCommandDto {
    // DTO 내용 수정 필요
    private String type;
    private String ipAddress;
    private String protocol;
    private String port;
    private String chain;
    private String action;
    private long timestamp;
}
