package com.kr.api_gateway.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kr.core.web.exception.BusinessException;
import com.kr.core.web.exception.handler.BaseGlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@RestControllerAdvice
public class GatewayGlobalExceptionHandler extends BaseGlobalExceptionHandler {

    public GatewayGlobalExceptionHandler(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<JsonNode> handleBusinessException(BusinessException e) {
        HttpStatus httpStatus = e.getHttpStatus();
        String errorMessage = e.getMessage();
        String errorCode = e.getErrorCode();

        log.error("BusinessException 발생: 상태 코드={}, 에러 코드={}, 메시지={}",
                httpStatus, errorCode, errorMessage);

        ObjectNode responseBody = objectMapper.createObjectNode();
        responseBody.put("status", httpStatus.value());
        responseBody.put("message", errorMessage);

        if (errorCode != null) {
            responseBody.put("errorCode", errorCode);
        }

        return ResponseEntity.status(httpStatus).body(responseBody);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<JsonNode> handleWebClientResponseException(WebClientResponseException e) {
        log.error("WebClientResponseException 발생: 상태 코드={}, 응답 본문={}", e.getRawStatusCode(), e.getResponseBodyAsString(), e);

        ObjectNode responseBody = objectMapper.createObjectNode();
        responseBody.put("status", e.getRawStatusCode());
        responseBody.put("message", "다운스트림 서비스 호출에 실패했습니다.");
        // We could try to parse the response body from the downstream service if it's JSON
        try {
            JsonNode errorBody = objectMapper.readTree(e.getResponseBodyAsString());
            responseBody.set("downstreamError", errorBody);
        } catch (Exception parseException) {
            responseBody.put("downstreamError", e.getResponseBodyAsString());
        }


        return ResponseEntity.status(e.getRawStatusCode()).body(responseBody);
    }

    @Override
    @ExceptionHandler(Exception.class)
    public ResponseEntity<JsonNode> handleException(Exception e) {
        log.error("처리되지 않은 예외 발생", e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "API 게이트웨이 내부 오류가 발생했습니다.", "E999");
    }
} 