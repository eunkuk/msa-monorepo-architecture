package com.kr.api_gateway.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 오디오 WebSocket 핸들러
 * 클라이언트와의 WebSocket 연결을 처리하고 오디오 데이터를 관리합니다.
 * 세션 관리, 오디오 녹음 시작/중지, 하트비트 처리 등의 기능을 제공합니다.
 */
@Slf4j
@Component
public class AudioHandler implements WebSocketHandler {

    private final ObjectMapper objectMapper;
    private final WebClient audioServerWebClient;
    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();
    private final Map<String, Long> sessionLastActivityMap = new ConcurrentHashMap<>();

    public AudioHandler(ObjectMapper objectMapper, WebClient audioServerWebClient) {
        this.objectMapper = objectMapper;
        this.audioServerWebClient = audioServerWebClient;
    }

    /**
     * WebSocket 연결을 처리하는 메인 메서드
     * 클라이언트로부터 메시지를 수신하고 처리합니다.
     * 연결이 종료되면 세션 리소스를 정리합니다.
     *
     * @param session WebSocket 세션
     * @return 비동기 처리 결과
     */
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(payload -> processPayload(session, payload))
                .doFinally(signalType -> {
                    String sessionId = session.getId();
                    log.info("WebSocket 연결 종료: 세션 {}", sessionId);

                    // AudioHandler에서 세션 정리
                    sessionUserMap.remove(sessionId);
                    sessionLastActivityMap.remove(sessionId);

                    // RecordService에 리소스 정리 요청
                    audioServerWebClient.post()
                        .uri("/record/end")
                        .bodyValue(Map.of("sessionId", sessionId))
                        .retrieve()
                        .bodyToMono(String.class)
                        .subscribe(
                            response -> log.info("녹음 종료 응답: {}", response),
                            error -> log.error("녹음 종료 오류: {}", error.getMessage())
                        );
                })
                .then();
    }

    /**
     * 클라이언트로부터 수신한 메시지를 처리하는 메서드
     * JSON 형식의 메시지를 파싱하고 action 필드에 따라 적절한 핸들러로 라우팅합니다.
     *
     * @param session WebSocket 세션
     * @param payload 클라이언트로부터 수신한 메시지 내용
     * @return 비동기 처리 결과
     */
    private Mono<Void> processPayload(WebSocketSession session, String payload) {
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            String action = jsonNode.has("action") ? jsonNode.get("action").asText() : null;

            log.info("Session ID: {}", session.getId());
            // session id 전달
            ObjectNode enhancedJsonNode = (ObjectNode) jsonNode;
            enhancedJsonNode.put("sessionId", session.getId());
            enhancedJsonNode.put("connectionId", session.getId());

            // 세션 활동 시간 업데이트
            String sessionId = session.getId();
            sessionLastActivityMap.put(sessionId, System.currentTimeMillis());

            log.info("Received action: {}", action);
            switch (action) {
                case "record-start" -> {
                    return handleRecordStart(session, jsonNode);
                }
                case "record-idle" -> {
                    return handleRecordIdle(session, jsonNode);
                }
                case "record-end" -> {
                    return handleRecordEnd(session, jsonNode);
                }
                case "heartbeat" -> {
                    return handleHeartbeat(session, jsonNode);
                }
                default -> {
                    log.info("Unknown action");
                    return sendError(session, "Unknown action");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return sendError(session, "Error processing message");
        }
    }

    /**
     * 녹음 시작 요청을 처리하는 메서드
     * 사용자 ID를 세션 맵에 저장하고 오디오 서버에 녹음 시작 요청을 전달합니다.
     *
     * @param session WebSocket 세션
     * @param jsonNode 클라이언트로부터 수신한 JSON 데이터
     * @return 비동기 처리 결과
     */
    private Mono<Void> handleRecordStart(WebSocketSession session, JsonNode jsonNode) {
        // 사용자 ID 추출 및 저장
        String userId = jsonNode.get("metaData").get("userId").asText();
        sessionUserMap.put(session.getId(), userId);

        // metaId가 있으면 사용, 없으면 세션 ID 사용
        String metaId = jsonNode.has("metaId") ? jsonNode.get("metaId").asText() : session.getId();

        // metaId를 jsonNode에 추가 (이미 있으면 덮어씀)
        ((com.fasterxml.jackson.databind.node.ObjectNode) jsonNode).put("metaId", metaId);

        log.info("Record start with metaId: {}", metaId);

        return audioServerWebClient.post()
                .uri("/record/start")
                .bodyValue(jsonNode)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> session.send(Mono.just(session.textMessage("Start record response: " + response))));
    }

    /**
     * 녹음 중 오디오 데이터 청크를 처리하는 메서드
     * 세션이 유효한지 확인하고 오디오 서버에 데이터를 전달합니다.
     *
     * @param session WebSocket 세션
     * @param jsonNode 클라이언트로부터 수신한 JSON 데이터 (오디오 청크 포함)
     * @return 비동기 처리 결과
     */
    private Mono<Void> handleRecordIdle(WebSocketSession session, JsonNode jsonNode) {
        if (!sessionUserMap.containsKey(session.getId())) {
            return sendError(session, "Error: Session user data not found");
        }

        // metaId가 없으면 세션 ID 사용
        if (!jsonNode.has("metaId") || jsonNode.get("metaId").isNull()) {
            ((com.fasterxml.jackson.databind.node.ObjectNode) jsonNode).put("metaId", session.getId());
            log.info("Using session ID as metaId for idle chunk: {}", session.getId());
        } else {
            log.info("Using provided metaId for idle chunk: {}", jsonNode.get("metaId").asText());
        }

        return audioServerWebClient.post()
                .uri("/record/idle")
                .bodyValue(jsonNode)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> session.send(Mono.just(session.textMessage("Idle chunk processed: " + response))));
    }

    /**
     * 오류 메시지를 클라이언트에게 전송하는 메서드
     *
     * @param session WebSocket 세션
     * @param message 전송할 오류 메시지
     * @return 비동기 처리 결과
     */
    private Mono<Void> sendError(WebSocketSession session, String message) {
        return session.send(Mono.just(session.textMessage(message)));
    }

    /**
     * 녹음 종료 요청을 처리하는 메서드
     * 세션 정보를 정리하고 오디오 서버에 녹음 종료 요청을 전달합니다.
     *
     * @param session WebSocket 세션
     * @param jsonNode 클라이언트로부터 수신한 JSON 데이터
     * @return 비동기 처리 결과
     */
    private Mono<Void> handleRecordEnd(WebSocketSession session, JsonNode jsonNode) {
        String sessionId = session.getId();
        log.info("Handling record-end for session: {}", sessionId);

        // AudioHandler에서 세션 정리
        sessionUserMap.remove(sessionId);

        return audioServerWebClient.post()
                .uri("/record/end")
                .bodyValue(Map.of("sessionId", sessionId))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> session.send(Mono.just(session.textMessage("End record response: " + response))));
    }

    /**
     * 하트비트 요청을 처리하는 메서드
     * 세션의 마지막 활동 시간을 업데이트하고 클라이언트에게 응답을 전송합니다.
     *
     * @param session WebSocket 세션
     * @param jsonNode 클라이언트로부터 수신한 JSON 데이터
     * @return 비동기 처리 결과
     */
    private Mono<Void> handleHeartbeat(WebSocketSession session, JsonNode jsonNode) {
        String sessionId = session.getId();
        log.info("Received heartbeat from session: {}", sessionId);
        sessionLastActivityMap.put(sessionId, System.currentTimeMillis());
        return session.send(Mono.just(session.textMessage("Heartbeat acknowledged")));
    }

    /**
     * 오래된 세션을 정리하는 스케줄링 메서드
     * 30분 이상 활동이 없는 세션을 식별하고 리소스를 정리합니다.
     * 5분마다 자동으로 실행됩니다.
     */
    @Scheduled(fixedRate = 300000) // 5분마다 실행
    public void cleanupStaleSessions() {
        long currentTime = System.currentTimeMillis();
        List<String> staleSessions = new ArrayList<>();

        // 오래된 세션 식별 (30분 이상 비활성)
        for (Map.Entry<String, Long> entry : sessionLastActivityMap.entrySet()) {
            if (currentTime - entry.getValue() > 1800000) { // 30분
                staleSessions.add(entry.getKey());
            }
        }

        // 오래된 세션 정리
        for (String sessionId : staleSessions) {
            log.info("Cleaning up stale session: {}", sessionId);
            sessionUserMap.remove(sessionId);

            // RecordService에 리소스 정리 요청
            audioServerWebClient.post()
                .uri("/record/end")
                .bodyValue(Map.of("sessionId", sessionId))
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                    response -> log.info("녹음 종료 응답 (stale session): {}", response),
                    error -> log.error("녹음 종료 오류 (stale session): {}", error.getMessage())
                );

            sessionLastActivityMap.remove(sessionId);
        }
    }
}
