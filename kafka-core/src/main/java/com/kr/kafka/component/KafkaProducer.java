package com.kr.kafka.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public <T> void sendMessage(String topic, T message) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("메시지 전송 성공: topic={}, partition={}, offset={}, message={}",
                        topic, result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(), message);
            } else {
                log.error("메시지 전송 실패: topic={}, message={}", topic, message, ex);
            }
        });
    }

    public <T> void sendMessage(String topic, String key, T message) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("메시지 전송 성공: topic={}, key={}, partition={}, offset={}, message={}",
                        topic, key, result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(), message);
            } else {
                log.error("메시지 전송 실패: topic={}, key={}, message={}", topic, key, message, ex);
            }
        });
    }
}
