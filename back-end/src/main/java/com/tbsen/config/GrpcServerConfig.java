package com.tbsen.config;

import io.grpc.netty.NettyServerBuilder;
import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

@Configuration
public class GrpcServerConfig {

    @Bean
    public GrpcServerConfigurer keepAliveConfigurer() {
        return serverBuilder -> {
            if (serverBuilder instanceof NettyServerBuilder) {
                System.out.println("[gRPC] Netty Keepalive 설정 적용");
                
                ((NettyServerBuilder) serverBuilder)
                    // 5s 주기 핑 허용 (기본값 2H)
                    .permitKeepAliveTime(5, TimeUnit.SECONDS)
                    
                    // 데이터 전송 없어도 핑 허용
                    .permitKeepAliveWithoutCalls(true)

                    .keepAliveTime(30, TimeUnit.SECONDS)
                    .keepAliveTimeout(10, TimeUnit.SECONDS);
            }
        };
    }
}