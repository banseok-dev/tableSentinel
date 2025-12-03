package internal.app.tableSentinel.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NftCommandDto extends BaseCommandDto {
    private String table;
    private String chain;
    private String targetIp;
    private String action;
}
