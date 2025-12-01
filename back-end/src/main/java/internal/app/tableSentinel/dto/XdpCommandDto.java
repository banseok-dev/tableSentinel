package internal.app.tableSentinel.dto;

import lombok.Data;

@Data
public class XdpCommandDto {

    private String type;
    private String ipAddress;
    private String mode = "src,dst";
    private long timestamp;
}
