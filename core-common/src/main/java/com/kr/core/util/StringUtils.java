package com.kr.core.util;

/**
 * 문자열 관련 유틸리티 클래스
 * 문자열 처리를 위한 다양한 유틸리티 메서드를 제공합니다.
 */
public class StringUtils {
    
    /**
     * 문자열이 null이거나 비어있는지 확인합니다.
     * 공백 문자만 있는 경우도 비어있는 것으로 간주합니다.
     *
     * @param str 검사할 문자열
     * @return 문자열이 null이거나 비어있으면 true, 그렇지 않으면 false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 문자열이 null이 아니고 비어있지 않은지 확인합니다.
     * 공백 문자만 있는 경우는 비어있는 것으로 간주합니다.
     *
     * @param str 검사할 문자열
     * @return 문자열이 null이 아니고 비어있지 않으면 true, 그렇지 않으면 false
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * null 문자열을 빈 문자열로 변환합니다.
     * null이 아닌 문자열은 그대로 반환합니다.
     *
     * @param str 변환할 문자열
     * @return null이면 빈 문자열(""), 그렇지 않으면 원래 문자열
     */
    public static String nullToEmpty(String str) {
        return str == null ? "" : str;
    }
    
    /**
     * 문자열의 첫 글자를 대문자로 변환합니다.
     * 빈 문자열이나 null인 경우 원래 값을 반환합니다.
     *
     * @param str 변환할 문자열
     * @return 첫 글자가 대문자로 변환된 문자열
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
    
    /**
     * 문자열이 지정된 접두사로 시작하는지 확인합니다.
     * 대소문자를 구분하지 않습니다.
     *
     * @param str 검사할 문자열
     * @param prefix 확인할 접두사
     * @return 문자열이 지정된 접두사로 시작하면 true, 그렇지 않으면 false
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        
        if (prefix.length() > str.length()) {
            return false;
        }
        
        return str.regionMatches(true, 0, prefix, 0, prefix.length());
    }
}