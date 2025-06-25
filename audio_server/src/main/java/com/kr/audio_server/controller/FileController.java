package com.kr.audio_server.controller;

import com.kr.audio_server.exception.AudioServiceErrorCode;
import com.kr.core.web.exception.BusinessException;
import com.kr.core.web.exception.DefaultErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 파일 관리 컨트롤러
 * 녹음된 오디오 파일 목록 조회 및 다운로드 기능을 제공합니다.
 */
@Slf4j
@RestController
public class FileController {

    private static final String RECORDINGS_PATH = System.getProperty("user.dir") + File.separator + "recordings";

    /**
     * 녹음된 파일 목록을 조회하는 메서드
     * recordings 디렉토리에 저장된 모든 파일의 이름을 반환합니다.
     *
     * @return 파일 이름 목록
     */
    @GetMapping("/api/files")
    public List<String> getFiles() {
        File folder = new File(RECORDINGS_PATH);
        File[] listOfFiles = folder.listFiles();
        List<String> fileNames = new ArrayList<>();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }
        return fileNames;
    }

    /**
     * 녹음된 파일을 다운로드하는 메서드
     * 지정된 파일 이름에 해당하는 파일을 다운로드할 수 있는 응답을 반환합니다.
     *
     * @param fileName 다운로드할 파일 이름
     * @return 파일 다운로드 응답
     */
    @GetMapping("/api/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(RECORDINGS_PATH).resolve(fileName);
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new BusinessException(DefaultErrorCode.RESOURCE_NOT_FOUND);
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            log.error("[DOWNLOAD] [ERR] Error creating resource for file: {}", fileName, e);
            throw new BusinessException(AudioServiceErrorCode.FILE_PROCESSING_ERROR);
        }
    }
}
