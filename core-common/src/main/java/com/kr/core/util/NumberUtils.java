package com.kr.core.util;

/**
 * 숫자(Number) 관련 유틸리티 클래스
 * 숫자 처리 및 변환을 위한 다양한 메서드를 제공합니다.
 */
public class NumberUtils {
    /**
     * 문자열이 숫자인지 확인합니다.
     *
     * @param str 검사할 문자열
     * @return 숫자이면 true, 아니면 false
     */
    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    /**
     * null 또는 빈 문자열을 0으로 변환합니다.
     *
     * @param str 변환할 문자열
     * @return 변환된 int 값
     */
    public static int parseIntOrZero(String str) {
        if (str == null || str.trim().isEmpty()) return 0;
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * null 또는 빈 문자열을 0L로 변환합니다.
     *
     * @param str 변환할 문자열
     * @return 변환된 long 값
     */
    public static long parseLongOrZero(String str) {
        if (str == null || str.trim().isEmpty()) return 0L;
        try {
            return Long.parseLong(str.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
} 