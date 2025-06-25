package com.kr.kafka.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Kafka Core 모듈의 자동 설정 클래스
 * 이 클래스가 있으면 kafka-core 모듈을 의존성으로 추가한 프로젝트에서
 * 자동으로 Kafka 관련 빈들이 등록됩니다.
 */
@Configuration
@ComponentScan(basePackages = "com.kr.kafka.component")
@Import({KafkaProducerConfig.class, KafkaConsumerConfig.class})
public class KafkaAutoConfiguration {
    
    // 추가적인 설정이 필요한 경우 여기에 빈들을 정의할 수 있습니다.
} 