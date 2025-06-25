package com.kr.kafka.config;

import java.time.LocalDateTime;

public interface Event {
    String getTopic();
    LocalDateTime getTimestamp();
}