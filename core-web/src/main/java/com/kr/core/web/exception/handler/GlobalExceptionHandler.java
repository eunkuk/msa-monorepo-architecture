package com.kr.core.web.exception.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kr.core.web.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 전역 예외 처리 핸들러
 * 애플리케이션에서 발생하는 예외를 일관된 형식으로 처리합니다.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends BaseGlobalExceptionHandler {

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        super(objectMapper);
    }
    
    /**
     * BusinessException 처리
     * 비즈니스 로직 관련 예외를 처리하고 적절한 응답을 반환합니다.
     * 
     * @param e 발생한 BusinessException
     * @param redirectAttributes 리다이렉트 속성
     * @return 에러 정보를 담은 JSON 응답
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<JsonNode> handleBusinessException(BusinessException e, RedirectAttributes redirectAttributes) {
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
    
    /**
     * 일반 Exception 처리
     * 처리되지 않은 예외를 포착하여 일관된 응답을 반환합니다.
     * 
     * @param e 발생한 Exception
     * @return 에러 정보를 담은 JSON 응답
     */
    @Override
    @ExceptionHandler(Exception.class)
    public ResponseEntity<JsonNode> handleException(Exception e) {
        log.error("처리되지 않은 예외 발생", e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.", "E999");
    }
}