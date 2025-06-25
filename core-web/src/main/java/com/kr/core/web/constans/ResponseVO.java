package com.kr.core.web.constans;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseVO<T> {

    // 부정 선언
    private String code = ResponseCodes.ERROR.getCode();
    private String messages = ResponseCodes.ERROR.getMessage();
    private T data;

    public ResponseVO<T> successData(T data) {
        this.setCode(ResponseCodes.SUCCESS.getCode());
        this.setMessages(ResponseCodes.SUCCESS.getMessage());
        this.setData(data);
        return this;
    }

    public ResponseVO setResponseCode(ResponseCodes responseCode) {
        this.setCode(responseCode.getCode());
        this.setMessages(responseCode.getMessage());
        return this;
    }

}