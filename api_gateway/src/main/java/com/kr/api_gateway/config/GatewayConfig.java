package com.kr.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("websocket_route", r -> r
                        .path("/ws/**")
                        .uri("ws://localhost:5000"))
                .route("audio_server_files", r -> r
                        .path("/api/files")
                        .uri("lb://audio-server"))
                .route("audio_server_download", r -> r
                        .path("/api/download/**")
                        .uri("lb://audio-server"))
                .build();
    }

}
