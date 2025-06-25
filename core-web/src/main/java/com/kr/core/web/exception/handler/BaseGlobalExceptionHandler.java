package com.kr.core.web.exception.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kr.core.web.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseGlobalExceptionHandler {

    protected final ObjectMapper objectMapper;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<JsonNode> handleBusinessException(BusinessException e) {
        HttpStatus httpStatus = e.getHttpStatus();
        String errorMessage = e.getMessage();
        String errorCode = e.getErrorCode();

        log.error("BusinessException 발생: 상태 코드={}, 에러 코드={}, 메시지={}",
                httpStatus, errorCode, errorMessage);

        return buildErrorResponse(httpStatus, errorMessage, errorCode);
    }

    protected ResponseEntity<JsonNode> buildErrorResponse(HttpStatus status, String message, String errorCode) {
        ObjectNode responseBody = objectMapper.createObjectNode();
        responseBody.put("status", status.value());
        responseBody.put("message", message);
        if (errorCode != null) {
            responseBody.put("errorCode", errorCode);
        }
        return ResponseEntity.status(status).body(responseBody);
    }

    @ExceptionHandler(Exception.class)
    public abstract ResponseEntity<JsonNode> handleException(Exception e);
} 