package com.kr.core.web.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // 실행할 모듈에서 yml 설정이 필요함.
    @Value("${services.audio-server.base-url}")
    private String audioServerBaseUrl;

    @Bean
    public WebClient audioServerWebClient() {
        return WebClient.builder()
                .baseUrl(audioServerBaseUrl)
                .build();
    }
    
} 