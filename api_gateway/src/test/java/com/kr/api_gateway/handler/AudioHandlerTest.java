package com.kr.api_gateway.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AudioHandlerTest {
    @Test
    void audioHandlerCanBeCreatedWithMockWebClient() {
        ObjectMapper objectMapper = new ObjectMapper();
        WebClient mockWebClient = mock(WebClient.class);
        AudioHandler handler = new AudioHandler(objectMapper, mockWebClient);
        assertThat(handler).isNotNull();
    }
} 