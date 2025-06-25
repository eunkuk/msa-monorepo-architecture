package com.kr.kafka.component;

import com.kr.kafka.config.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final KafkaProducer kafkaProducer;

    public <T extends Event> void publish(T event) {
        String topic = event.getTopic();
        kafkaProducer.sendMessage(topic, event);
    }

    public <T extends Event> void publish(T event, String key) {
        String topic = event.getTopic();
        kafkaProducer.sendMessage(topic, key, event);
    }
}