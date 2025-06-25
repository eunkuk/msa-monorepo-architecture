package com.kr.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 날짜 관련 유틸리티 클래스
 * 다양한 형식의 날짜 포맷팅 기능을 제공합니다.
 */
public class DateUtils {
    
    /**
     * 날짜를 지정된 패턴으로 포맷팅합니다.
     *
     * @param date 포맷팅할 날짜
     * @param pattern 날짜 포맷 패턴 (예: "yyyyMMdd_HHmmssSSS")
     * @return 포맷팅된 날짜 문자열
     */
    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }
    
    /**
     * 현재 날짜와 시간을 "yyyyMMdd_HHmmssSSS" 형식으로 반환합니다.
     * 파일명 생성 등에 사용됩니다.
     *
     * @return 현재 타임스탬프 문자열
     */
    public static String getCurrentTimestamp() {
        return formatDate(new Date(), "yyyyMMdd_HHmmssSSS");
    }
}