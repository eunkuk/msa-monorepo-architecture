package com.kr.audio_server.exception;

import com.kr.core.web.exception.ErrorCodeProvider;

public enum AudioServiceErrorCode implements ErrorCodeProvider {

    FILE_PROCESSING_ERROR("A001", "파일 처리 중 오류가 발생했습니다.");

    private final String code;
    private final String defaultMessage;

    AudioServiceErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }
} 