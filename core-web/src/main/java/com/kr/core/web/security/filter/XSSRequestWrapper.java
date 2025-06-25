package com.kr.core.web.security.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * XSS 공격 방지를 위한 HTTP 요청 래퍼
 * 요청 파라미터, 헤더 등에서 잠재적인 XSS 공격 패턴을 제거합니다.
 */
public class XSSRequestWrapper extends HttpServletRequestWrapper {

    private static final Pattern[] PATTERNS = new Pattern[]{
            // Script 태그 제거
            Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // src 속성 값에 대한 검사
            Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // 악성 코드 패턴 제거
            Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
            Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
            // 이벤트 핸들러 제거
            Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onclick(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onmouseover(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onmouseout(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onmousedown(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
    };

    /**
     * 생성자
     * @param servletRequest 원본 HTTP 요청
     */
    public XSSRequestWrapper(HttpServletRequest servletRequest) {
        super(servletRequest);
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = stripXSS(values[i]);
        }
        return encodedValues;
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        return stripXSS(value);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return stripXSS(value);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameterMap = super.getParameterMap();
        Map<String, String[]> cleanMap = new HashMap<>();
        Set<String> keySet = parameterMap.keySet();
        Iterator<String> iterator = keySet.iterator();

        while (iterator.hasNext()) {
            String key = iterator.next();
            String[] paramValues = parameterMap.get(key);
            if (paramValues == null) {
                cleanMap.put(key, null);
            } else {
                int count = paramValues.length;
                String[] encodedValues = new String[count];
                for (int i = 0; i < count; i++) {
                    encodedValues[i] = stripXSS(paramValues[i]);
                }
                cleanMap.put(key, encodedValues);
            }
        }
        return cleanMap;
    }

    /**
     * XSS 공격 패턴을 제거하는 메서드
     * @param value 검사할 문자열
     * @return XSS 패턴이 제거된 문자열
     */
    private String stripXSS(String value) {
        if (value != null) {
            // 특수 문자 인코딩
            value = value.replaceAll("[<>]", "&#?;");
            value = value.replaceAll("\"", "&quot;");

            // XSS 패턴 제거
            for (Pattern pattern : PATTERNS) {
                value = pattern.matcher(value).replaceAll("");
            }

            // 이벤트 핸들러 제거
            value = removeEventHandlers(value);

            // 악성 태그 및 키워드 제거
            value = removeMaliciousTags(value);
        }
        return value;
    }

    /**
     * 이벤트 핸들러를 제거하는 메서드
     * @param value 검사할 문자열
     * @return 이벤트 핸들러가 제거된 문자열
     */
    private String removeEventHandlers(String value) {
        // onclick, onmouseover 등의 이벤트 핸들러 제거
        return value.replaceAll("<a\\s+onclick\\s*=\\s*(\"|').*?\\1.*?>", "<a>");
    }

    /**
     * 악성 태그 및 키워드를 제거하는 메서드
     * @param value 검사할 문자열
     * @return 악성 태그 및 키워드가 제거된 문자열
     */
    private String removeMaliciousTags(String value) {
        // 악성 태그 및 키워드 제거
        value = value.replaceAll("document", "x-document");
        value = value.replaceAll("applet", "x-applet");
        value = value.replaceAll("object", "x-object");
        value = value.replaceAll("frame", "x-frame");
        value = value.replaceAll("frameset", "x-frameset");
        value = value.replaceAll("layer", "x-layer");
        value = value.replaceAll("bgsound", "x-bgsound");
        value = value.replaceAll("alert", "x-alert");
        value = value.replaceAll("onblur", "x-onblur");
        value = value.replaceAll("onchange", "x-onchange");
        value = value.replaceAll("ondblclick", "x-ondblclick");
        value = value.replaceAll("enerror", "x-enerror");
        value = value.replaceAll("onfocus", "x-onfocus");
        value = value.replaceAll("onmouse", "x-onmouse");
        value = value.replaceAll("onscroll", "x-onscroll");
        value = value.replaceAll("onsubmit", "x-onsubmit");
        value = value.replaceAll("onunload", "x-onunload");
        return value;
    }
}