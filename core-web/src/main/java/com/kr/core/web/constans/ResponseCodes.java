package com.kr.core.web.constans;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCodes {
    SUCCESS(
            "0000",
            "성공"
    ),
    ERROR(
            "9999",
            "프로세싱중 에러가 발생하였습니다."
    ),
    AUTH_FAIL(
            "5000",
            "로그인 및 인증 실패하였습니다."
    ),
    TOKEN_FAIL(
            "5000",
            "토큰 생성 실패하였습니다."
    ),
    REQUIRED_PARAMETERS(
            "9000",
            "필수값을 확인 하세요."
    ),
    MISSING_DATA(
            "9001",
            "데이터가 누락 되었습니다."
    );

    private final String code;
    private final String message;

}