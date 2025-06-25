package com.kr.core.web.exception;

public enum DefaultErrorCode implements ErrorCodeProvider {
    INVALID_REQUEST("E001", "잘못된 요청입니다."),
    RESOURCE_NOT_FOUND("E002", "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR("E999", "서버 내부 오류가 발생했습니다.");

    private final String code;
    private final String defaultMessage;

    DefaultErrorCode(String code, String defaultMessage) {
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