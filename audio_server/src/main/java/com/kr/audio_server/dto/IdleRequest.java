package com.kr.audio_server.dto;

import lombok.Data;

@Data
public class IdleRequest {

    private String action;
    private String sessionId;
    private String chunk;
    private String metaId;

}
