package com.kr.core.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 파일 관련 유틸리티 클래스
 * 파일 처리를 위한 다양한 유틸리티 메서드를 제공합니다.
 */
public class FileUtils {
    
    /**
     * 파일 확장자를 추출합니다.
     * 파일명에 확장자가 없는 경우 빈 문자열을 반환합니다.
     *
     * @param fileName 파일명
     * @return 파일 확장자 (점 제외)
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex + 1);
    }
    
    /**
     * 파일명에서 확장자를 제외한 이름을 추출합니다.
     *
     * @param fileName 파일명
     * @return 확장자를 제외한 파일명
     */
    public static String getFileNameWithoutExtension(String fileName) {
        if (fileName == null) return "";
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? fileName : fileName.substring(0, lastDotIndex);
    }
    
    /**
     * 디렉토리가 존재하지 않으면 생성합니다.
     *
     * @param directoryPath 생성할 디렉토리 경로
     * @return 디렉토리 생성 성공 여부
     */
    public static boolean createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            return directory.mkdirs();
        }
        return true;
    }
    
    /**
     * 지정된 디렉토리에서 모든 파일 목록을 가져옵니다.
     * 하위 디렉토리는 포함하지 않습니다.
     *
     * @param directoryPath 파일을 검색할 디렉토리 경로
     * @return 파일 이름 목록
     */
    public static List<String> listFiles(String directoryPath) {
        List<String> fileNames = new ArrayList<>();
        File directory = new File(directoryPath);
        
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileNames.add(file.getName());
                    }
                }
            }
        }
        
        return fileNames;
    }
    
    /**
     * 지정된 디렉토리에서 특정 확장자를 가진 모든 파일 목록을 가져옵니다.
     *
     * @param directoryPath 파일을 검색할 디렉토리 경로
     * @param extension 검색할 파일 확장자 (점 제외)
     * @return 파일 이름 목록
     */
    public static List<String> listFilesByExtension(String directoryPath, String extension) {
        List<String> fileNames = new ArrayList<>();
        File directory = new File(directoryPath);
        
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith("." + extension)) {
                        fileNames.add(file.getName());
                    }
                }
            }
        }
        
        return fileNames;
    }
    
    /**
     * 파일 크기를 사람이 읽기 쉬운 형식으로 변환합니다.
     * (예: 1024 -> "1 KB")
     *
     * @param size 파일 크기 (바이트)
     * @return 사람이 읽기 쉬운 형식의 파일 크기
     */
    public static String humanReadableByteCount(long size) {
        if (size < 1024) return size + " B";
        int exp = (int) (Math.log(size) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", size / Math.pow(1024, exp), pre);
    }
    
    /**
     * 파일의 MIME 타입을 추측합니다.
     *
     * @param filePath 파일 경로
     * @return MIME 타입 문자열, 확인할 수 없는 경우 "application/octet-stream"
     */
    public static String getMimeType(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.probeContentType(path);
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }
}