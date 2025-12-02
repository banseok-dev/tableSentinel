package internal.app.tableSentinel.dto;

import lombok.Data;

// Todo: nftables DTO 구성 필요함
@Data
public class XdpCommandDto {
    private String type;
    private String ipAddress;
    private String mode = "src,dst";
    private long timestamp;
}
