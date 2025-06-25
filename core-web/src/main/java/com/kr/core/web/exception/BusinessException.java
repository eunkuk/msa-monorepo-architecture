package com.kr.core.web.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 웹 비즈니스 예외 클래스
 * 웹 애플리케이션에서 발생하는 비즈니스 로직 관련 예외를 처리합니다.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String message;
    private final String errorCode;

    /**
     * 메시지와 HTTP 상태 코드로 예외를 생성합니다.
     * 
     * @param message 에러 메시지
     * @param httpStatus HTTP 상태 코드
     */
    public BusinessException(final String message, final HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.errorCode = null;
    }

    /**
     * 메시지, 에러 코드, HTTP 상태 코드로 예외를 생성합니다.
     * 
     * @param message 에러 메시지
     * @param code 에러 코드
     * @param httpStatus HTTP 상태 코드
     */
    public BusinessException(final String message, final String code, final HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.errorCode = code;
    }

    /**
     * ErrorCodeProvider로 예외를 생성합니다.
     *
     * @param errorCodeProvider 에러 코드 제공자
     */
    public BusinessException(ErrorCodeProvider errorCodeProvider) {
        this.message = errorCodeProvider.getDefaultMessage();
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.errorCode = errorCodeProvider.getCode();
    }

    /**
     * ErrorCodeProvider와 HTTP 상태 코드로 예외를 생성합니다.
     *
     * @param errorCodeProvider 에러 코드 제공자
     * @param httpStatus      HTTP 상태 코드
     */
    public BusinessException(ErrorCodeProvider errorCodeProvider, HttpStatus httpStatus) {
        this.message = errorCodeProvider.getDefaultMessage();
        this.httpStatus = httpStatus;
        this.errorCode = errorCodeProvider.getCode();
    }
}