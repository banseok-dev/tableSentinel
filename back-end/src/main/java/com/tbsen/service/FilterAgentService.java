package com.tbsen.service;

// DTO 주입
import com.tbsen.dto.NftCommandDto;
import com.tbsen.dto.ReportsDto;
import com.tbsen.dto.XdpCommandDto;
import com.tbsen.dto.AgentIdentityDto;

// gRPC
import com.tbsen.proto.*;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

// Lombok
import lombok.extern.slf4j.Slf4j;

// 자료구조
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;

// 그외 라이브러리
import java.util.UUID;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@GrpcService
public class FilterAgentService extends FilterAgentGrpc.FilterAgentImplBase {

    // 에이전트 채널 관리 (메모리 누수 방지용)
    private final Map<String, StreamObserver<Command>> agentChannels = new ConcurrentHashMap<>();

    // 에이전트 정보 관리 (프론트엔드 조회용)
    private final Map<String, AgentIdentityDto> connectedAgents = new ConcurrentHashMap<>();

    // 프론트 조회용 메서드
    public List<AgentIdentityDto> getConnectedAgents() {
        return new ArrayList<>(connectedAgents.values());
    }

    // 에이전트 등록 - rpc RegisterAgent
    @Override
    public void registerAgent(AgentIdentity request, StreamObserver<CommandResponse> responseObserver) {
        String uuid = request.getUuid();
        String hostname = request.getHostname();
        String ipAddress = request.getIpAddress();
        String agentVersion = request.getAgentVersion();

        log.info("[INFO] 에이전트 등록 요청 수신: UUID={}, Hostname={}, IP={}, AgentVersion={}", uuid, hostname, ipAddress, agentVersion);

        // TODO: 리팩터링 준비 필요
        // 생성자(new) 대신 Builder 사용
        AgentIdentityDto newAgent = AgentIdentityDto.builder()
                .uuid(uuid)
                .hostname(hostname)
                .agent_version(agentVersion)
                .ip_address(ipAddress)
                .last_heartbeat(LocalDateTime.now().toString()) // 서버 시간 기준
                .status("Online")
                .build();

        // Map에 저장
        connectedAgents.put(uuid, newAgent);

        // 응답 전송
        CommandResponse response = CommandResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Registered Successfully")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

// 상태 보고 (스트림) - rpc ReportStatus
    @Override
    public StreamObserver<AgentStatus> reportStatus(StreamObserver<CommandResponse> responseObserver) {
        return new StreamObserver<AgentStatus>() {
            @Override
            public void onNext(AgentStatus status) {
                log.info("[DEBUG-RAW] Incoming Status -> UUID: {}, Host: {}, IP: {}", 
                        status.getUuid(), 
                        status.getHostname(), 
                        status.getIpAddress());

                String uuid = status.getUuid();
                AgentIdentityDto agentInfo = connectedAgents.get(uuid);
            
                if (agentInfo != null) {
                    // 데이터 강제 갱신
                    agentInfo.setHostname(status.getHostname());
                    agentInfo.setIp_address(status.getIpAddress());
                    agentInfo.setStatus("Online");
                    agentInfo.setLast_heartbeat(LocalDateTime.now().toString());
                } else {
                    // 미등록 에이전트 처리
                    log.warn("[UNKNOWN] 미등록 에이전트 보고 수신: {}", uuid);
                    return; 
                }

                // 데이터 파싱 및 집계 (Proto -> DTO)
                long sumDrops = 0;
                long sumPass = 0;

                // XDP 상세 정보 순회
                // getXdpDetailsList()는 Proto의 repeated XdpInterfaceInfo xdp_details에 대응됨
                for (XdpInterfaceInfo info : status.getXdpDetailsList()) {
                    sumDrops += info.getDropCount();
                    sumPass += info.getPassCount();
                    
                    // 추후 인터페이스별 상세로그?
                    // log.debug("Iface: {}, Drop: {}", info.getName(), info.getDropCount());
                }

                // NFTables JSON 추출
                String nftJson = status.getNftablesRawJson();

                // DTO 빌드
                ReportsDto report = ReportsDto.builder()
                        .uuid(uuid)
                        .timestamp(LocalDateTime.ofInstant(Instant.ofEpochSecond(status.getTimestamp()), ZoneId.systemDefault()).toString())
                        .hostname(status.getHostname())
                        .ipAddress(status.getIpAddress())
                        .totalDropCount(sumDrops) // 집계된 총 차단 수
                        .totalPassCount(sumPass)
                        .xdpMode(status.getXdpMode().name()) // Enum -> String
                        .nftablesJson(nftJson) // 통짜 JSON
                        .build();

                // 데이터 저장
                log.info("[REPORT] UUID={} | Host={} | Drops={} | Mode={} | NFT_Len={}", 
                        report.getUuid(), 
                        report.getHostname(), 
                        report.getTotalDropCount(), 
                        report.getXdpMode(),
                        (nftJson != null ? nftJson.length() : 0));
                
                // TODO: repository.save(report); (DB 저장소 연결 시 활성화)
            }

            @Override
            public void onError(Throwable t) {
                log.warn("[STREAM ERROR] AgentStatus 스트림 중단: {}", t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(CommandResponse.newBuilder().setSuccess(true).build());
                responseObserver.onCompleted();
            }
        };
    }

    // 명령 결과 보고 - rpc ReportCommandResult
    @Override
    public void reportCommandResult(CommandResponse request, StreamObserver<CommandResponse> responseObserver) {
        // 에이전트 결과 찍기
        String cmdId = request.getCommandId();
        boolean success = request.getSuccess();
        String msg = request.getMessage();

        if (success) {
            log.info("[RESULT] Success: ID={}, Msg={}", cmdId, msg);
            // TODO: 나중에 DB에 처리 완료 상태 업데이트 로직 추가
        } else {
            log.error("[FAILED]Failed: ID={}, Msg={}", cmdId, msg);
            // TODO: 나중에 DB에 실패 상태 업데이트 및 알람 로직 추가
        }

        // 백엔드 -> 에이전트 ACK 응답
        CommandResponse ack = CommandResponse.newBuilder()
                .setCommandId(cmdId)
                .setSuccess(true)
                .setMessage("Server received the result.")
                .build();

        responseObserver.onNext(ack);
        responseObserver.onCompleted();
    }

    // 명령 대기열 구독 - rpc SubscribeCommands
    @Override
    public void subscribeCommands(AgentIdentity request, StreamObserver<Command> responseObserver) {
        String agentId = request.getUuid();
        agentChannels.put(agentId, responseObserver);
        
        log.info("[INFO] 에이전트 구독 연결됨: {} (현재 연결 수: {})", agentId, agentChannels.size());

        // 클라이언트 연결 끊김 감지 -> 맵에서 삭제
        Context.current().addListener(context -> {
            agentChannels.remove(agentId);
            log.warn("[INFO] 에이전트 연결 끊김: {}", agentId);
            AgentIdentityDto disconnectedAgent = connectedAgents.get(agentId);
            if (disconnectedAgent != null) {
                disconnectedAgent.setStatus("Offline");
            }
        }, java.util.concurrent.Executors.newSingleThreadExecutor());
    }


    // XDP Command 처리 로직
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

    // NFT Command 처리 로직
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

        // Handle (규칙 삭제 시 사용되는 고유 핸들 번호)
        if (hasText(dto.getHandle())) {
            nftBuilder.setHandle(dto.getHandle());
        }

        Command grpcCommand = Command.newBuilder()
                .setCommandId(UUID.randomUUID().toString()) // 추적 ID 생성
                .setNft(nftBuilder)
                .build();
        return pushCommand(agentId, grpcCommand);
    }

    // 문자열 유틸
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