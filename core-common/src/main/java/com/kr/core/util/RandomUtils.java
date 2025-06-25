package com.kr.core.util;

import java.security.SecureRandom;
import java.util.Random;

/**
 * 랜덤(Random) 관련 유틸리티 클래스
 * 랜덤 숫자, 문자열 등을 생성하는 메서드를 제공합니다.
 */
public class RandomUtils {
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new SecureRandom();

    /**
     * 지정한 범위 내의 랜덤 정수를 반환합니다.
     *
     * @param min 최소값(포함)
     * @param max 최대값(포함)
     * @return 랜덤 정수
     */
    public static int nextInt(int min, int max) {
        if (min > max) throw new IllegalArgumentException("min > max");
        return RANDOM.nextInt((max - min) + 1) + min;
    }

    /**
     * 지정한 길이의 랜덤 영숫자 문자열을 생성합니다.
     *
     * @param length 생성할 문자열 길이
     * @return 랜덤 문자열
     */
    public static String randomAlphanumeric(int length) {
        if (length <= 0) return "";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }
} 