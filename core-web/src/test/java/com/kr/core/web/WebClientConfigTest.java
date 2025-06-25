package com.kr.core.web;

import com.kr.core.web.webclient.WebClientConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = WebClientConfig.class)
@TestPropertySource(properties = "services.audio-server.base-url=http://mock-server:8080")
class WebClientConfigTest {

    @Autowired
    private WebClient audioServerWebClient;

    @Test
    void webClientBeanIsCreated() {
        assertThat(audioServerWebClient).isNotNull();
    }
}
