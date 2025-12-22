package com.tbsen.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;;

@Data
@SuperBuilder // 빌더 속성
@NoArgsConstructor
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "engineType",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = XdpCommandDto.class, name = "XDP"),      // XDP 라벨
    @JsonSubTypes.Type(value = NftCommandDto.class, name = "nftables")  // nftables 라벨
})

public class BaseCommandDto {
    protected String agentId;
    protected String commandType;
    protected long timestamp;
}
