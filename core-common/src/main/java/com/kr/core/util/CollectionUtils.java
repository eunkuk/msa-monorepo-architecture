package com.kr.core.util;

import java.util.Collection;
import java.util.Map;

/**
 * 컬렉션(Collection) 관련 유틸리티 클래스
 * 컬렉션 및 맵의 비어있음 여부 등을 확인하는 메서드를 제공합니다.
 */
public class CollectionUtils {
    /**
     * 컬렉션이 null이거나 비어있는지 확인합니다.
     *
     * @param collection 검사할 컬렉션
     * @return null이거나 비어있으면 true, 아니면 false
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 맵이 null이거나 비어있는지 확인합니다.
     *
     * @param map 검사할 맵
     * @return null이거나 비어있으면 true, 아니면 false
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 컬렉션이 null이 아니고 비어있지 않은지 확인합니다.
     *
     * @param collection 검사할 컬렉션
     * @return null이 아니고 비어있지 않으면 true, 아니면 false
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 맵이 null이 아니고 비어있지 않은지 확인합니다.
     *
     * @param map 검사할 맵
     * @return null이 아니고 비어있지 않으면 true, 아니면 false
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }
} 