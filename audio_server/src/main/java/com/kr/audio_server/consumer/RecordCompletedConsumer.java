package com.kr.audio_server.consumer;

import com.kr.audio_server.dto.RecordCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 파일 녹음 완료 이벤트 컨슈머
 */
@Slf4j
@Component
public class RecordCompletedConsumer {

    // (KafkaListener의 topic명은 실제 발행에 사용한 토픽과 동일해야 함)
    @KafkaListener(
            topics = "audio.record.completed",
            groupId = "audio-service"
    )
    public void consume(RecordCompletedEvent event) {
        log.info("[Kafka][RecordCompletedConsumer] 파일 처리 완료 이벤트 수신! sessionId={}, filePath={}",
                event.getSessionId(), event.getFilePath());

        // 1. 파일 후처리 (ex. 파일 이동, DB update, 썸네일 생성, 외부 API 호출 등)
        // 2. 비즈니스 로직 추가
        // 3. 필요하다면 추가 이벤트 발행

        // 예시: 파일이 S3에 저장됐다면 DB에 상태 저장
        // 예시: 썸네일/파생파일 생성 트리거 등

        // TODO: 구현
    }
}
