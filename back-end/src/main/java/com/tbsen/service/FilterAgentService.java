package com.tbsen.service;

import com.tbsen.dto.NftCommandDto;
import com.tbsen.dto.XdpCommandDto;
import com.tbsen.proto.*;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@GrpcService
public class FilterAgentService extends FilterAgentGrpc.FilterAgentImplBase {

    // 에이전트 채널 관리 (메모리 누수 방지용)
    private final Map<String, StreamObserver<Command>> agentChannels = new ConcurrentHashMap<>();

    // 에이전트 등록 (Handshake)
    @Override
    public void registerAgent(AgentIdentity request, StreamObserver<CommandResponse> responseObserver) {
        log.info("[INFO] 에이전트 등록 요청 수신: UUID={}, Host={}", request.getUuid(), request.getHostname());

        // TODO: DB에 에이전트 정보(OS, 커널 버전 등) 업데이트 로직이 여기 들어가면 됩니다.

        // 응답 생성 (성공)
        CommandResponse response = CommandResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Welcome to TableSentinel Controller!")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // 상태 보고 (Heartbeat + XDP Status)
    @Override
    public StreamObserver<AgentStatus> reportStatus(StreamObserver<CommandResponse> responseObserver) {
        return new StreamObserver<AgentStatus>() {
            @Override
            public void onNext(AgentStatus status) {
                // Agent가 보낸 데이터: UUID, Timestamp, 그리고 XDP 상태 정보
                log.info("[INFO] Agent: {}, Time: {}", status.getUuid(), status.getTimestamp());
                
                // TODO: 여기서 status.getXdpStatus() 등을 꺼내서 DB에 저장
                // 예: if (status.hasXdpStatus()) { ... }
            }

            @Override
            public void onError(Throwable t) {
                log.warn("[ERROR] 스트림 에러: {}", t.getMessage());
                // 에러 발생 시 맵에서 제거하는 로직은 subscribeCommands의 Context Listener가 처리하므로 여기선 로깅만
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(CommandResponse.newBuilder().setSuccess(true).build());
                responseObserver.onCompleted();
            }
        };
    }

    // 명령 대기열 구독
    @Override
    public void subscribeCommands(AgentIdentity request, StreamObserver<Command> responseObserver) {
        String agentId = request.getUuid();
        agentChannels.put(agentId, responseObserver);
        
        log.info("[INFO] 에이전트 구독 연결됨: {} (현재 연결 수: {})", agentId, agentChannels.size());

        // 클라이언트 연결 끊김 감지 -> 맵에서 삭제
        Context.current().addListener(context -> {
            agentChannels.remove(agentId);
            log.warn("[INFO] 에이전트 연결 끊김: {}", agentId);
        }, java.util.concurrent.Executors.newSingleThreadExecutor());
    }


    // XDP Command 처리 로직 (L2 Firewall)
    public boolean executeXdpCommand(String agentId, XdpCommandDto dto) {
        
        // 1. DTO -> Proto Builder 변환 (컨트롤러에서 가져온 로직)
        XdpCommand.Builder xdpBuilder = XdpCommand.newBuilder();

        // Action 변환
        if ("DELETE".equalsIgnoreCase(dto.getAction())) {
            xdpBuilder.setAction(ActionType.ACTION_DELETE);
        } else {
            xdpBuilder.setAction(ActionType.ACTION_ADD);
        }

        // IP 설정
        if (hasText(dto.getTargetIp())) {
            xdpBuilder.setTargetIp(dto.getTargetIp());
            xdpBuilder.setMode(dto.getMode() != null ? dto.getMode() : "src");
        }
        
        // MAC 설정
        if (hasText(dto.getTargetMac())) {
            xdpBuilder.setTargetMac(dto.getTargetMac());
        }

        // 인터페이스 설정
        if (hasText(dto.getInterfaceName())) {
            xdpBuilder.setInterfaceName(dto.getInterfaceName());
            if ("SKB".equalsIgnoreCase(dto.getInterfaceMode())) {
                xdpBuilder.setInterfaceMode(XdpMode.XDP_MODE_SKB);
            } else {
                xdpBuilder.setInterfaceMode(XdpMode.XDP_MODE_NATIVE);
            }
        }

        // Command gRPC 조립 후 전송
        Command grpcCommand = Command.newBuilder()
                .setCommandId(UUID.randomUUID().toString())
                .setXdp(xdpBuilder)
                .build();
        return pushCommand(agentId, grpcCommand);
    }

    // NFT Command 처리 로직 (L3/L4 Firewall)
    public boolean executeNftCommand(String agentId, NftCommandDto dto) {
        
        NftCommand.Builder nftBuilder = NftCommand.newBuilder();

        if ("DELETE".equalsIgnoreCase(dto.getAction())) {
            nftBuilder.setAction(ActionType.ACTION_DELETE);
        } else {
            nftBuilder.setAction(ActionType.ACTION_ADD);
        }

        // Chain (INPUT, OUTPUT, FORWARD 등)
        if (hasText(dto.getChain())) {
            nftBuilder.setChain(dto.getChain());
        }

        // Target IP
        if (hasText(dto.getTargetIp())) {
            nftBuilder.setTargetIp(dto.getTargetIp());
        }

        // Protocol (tcp, udp, icmp)
        if (hasText(dto.getProtocol())) {
            nftBuilder.setProtocol(dto.getProtocol());
        }

        // Port
        if (hasText(dto.getPort())) {
            nftBuilder.setPort(dto.getPort());
        }

        // Handle (규칙 삭제 시 사용되는 고유 번호)
        if (hasText(dto.getHandle())) {
            nftBuilder.setHandle(dto.getHandle());
        }

        Command grpcCommand = Command.newBuilder()
                .setCommandId(UUID.randomUUID().toString()) // 추적 ID 생성
                .setNft(nftBuilder)
                .build();
        return pushCommand(agentId, grpcCommand);
    }

    // [Helper] 문자열 유틸
    private boolean hasText(String str) {
        return str != null && !str.isEmpty();
    }

    // gRPC 전송 메서드
    private boolean pushCommand(String agentId, Command command) {
        StreamObserver<Command> observer = agentChannels.get(agentId);
        
        if (observer != null) {
            try {
                observer.onNext(command);
                log.info("[SUCCESS] gRPC 명령 전송 성공: Agent={}, ID={}", agentId, command.getCommandId());
                return true;
            } catch (Exception e) {
                log.error("[FAILED] gRPC 명령 전송 실패: {}", agentId);
                agentChannels.remove(agentId);
                return false;
            }
        } else {
            log.warn("[WARNING] 에이전트 미연결: {}", agentId);
            return false;
        }
    }
}