package com.kr.audio_server.service;

import com.kr.audio_server.dto.IdleRequest;
import com.kr.audio_server.dto.RecordCompletedEvent;
import com.kr.audio_server.dto.RecordRequest;
import com.kr.audio_server.exception.AudioServiceErrorCode;
import com.kr.core.util.DateUtils;
import com.kr.core.util.StringUtils;
import com.kr.core.web.exception.BusinessException;
import com.kr.kafka.component.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 오디오 녹음 서비스
 * 클라이언트로부터 수신한 오디오 데이터를 파일로 저장하고 관리합니다.
 * 세션별로 파일 스트림을 관리하며, 녹음 시작/중지 및 청크 데이터 저장 기능을 제공합니다.
 */
@Slf4j
@Service
public class RecordService {

    private final Map<String, String> filePathMap = new HashMap<>();
    private final Map<String, BufferedOutputStream> fileStreamMap = new HashMap<>();
    private final Map<String, String> metaIdMap = new HashMap<>();
    private final EventPublisher eventPublisher;

    private static final int BUFFER_SIZE = 32768;
    private static final String FILE_PATH = System.getProperty("user.dir") + File.separator + "recordings";

    public RecordService(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * 녹음 세션을 시작하는 메서드
     * 세션 ID에 해당하는 파일을 생성하고 출력 스트림을 초기화합니다.
     *
     * @param dto 녹음 시작 요청 정보를 담은 DTO
     */
    public void start(RecordRequest dto) {
        log.info("[start] ConnectionId: {}, MetaId: {}", dto.getSessionId(), dto.getMetaId());

        try {
            // metaId가 있으면 사용, 없으면 세션 ID 사용 (StringUtils 활용)
            String fileIdentifier = StringUtils.isEmpty(dto.getMetaId()) 
                    ? dto.getSessionId() 
                    : dto.getMetaId();

            // metaId 저장
            metaIdMap.put(dto.getSessionId(), dto.getMetaId());

            // core 모듈의 DateUtils 사용
            String now = DateUtils.getCurrentTimestamp();

            String fileName = String.format("%s_%s.webm", fileIdentifier, now);

            Files.createDirectories(Paths.get(FILE_PATH));
            String filePath = FILE_PATH + File.separator + fileName;

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath), BUFFER_SIZE);
            fileStreamMap.put(dto.getSessionId(), bos);
            filePathMap.put(dto.getSessionId(), filePath);

        } catch (IOException e) {
            log.error("[RECORD] [START] [ERR] Error : {}", e.getMessage(), e);
            throw new BusinessException(AudioServiceErrorCode.FILE_PROCESSING_ERROR);
        }
    }

    /**
     * 오디오 데이터 청크를 저장하는 메서드
     * Base64로 인코딩된 오디오 데이터를 디코딩하여 파일에 저장합니다.
     *
     * @param dto 오디오 데이터 청크를 담은 DTO
     */
    public void saveChunk(IdleRequest dto) {
        log.info("[saveChunk] ConnectionId: {}, MetaId: {}", dto.getSessionId(), dto.getMetaId());

        String connectionId = dto.getSessionId();
        String chunk = dto.getChunk();

        BufferedOutputStream bos = fileStreamMap.get(connectionId);
        if (bos == null) {
            log.error("[saveChunk] No open stream for sessionId: {}", connectionId);
            return;
        }

        try {
            String base64Data = chunk.split("base64,")[1];
            byte[] decodedData = Base64.getDecoder().decode(base64Data);
            bos.write(decodedData);
            bos.flush(); // 버퍼의 내용을 파일로 즉시 기록
        } catch (IOException e) {
            log.error("[RECORD] [IDLE] [ERR] Error : {}", e.getMessage());
            throw new BusinessException(AudioServiceErrorCode.FILE_PROCESSING_ERROR);
        }
    }

    /**
     * 녹음 세션을 종료하는 메서드
     * 파일 스트림을 닫고 세션 관련 리소스를 정리합니다.
     * 녹음 완료 시 카프카 이벤트를 발행합니다.
     *
     * @param sessionId 종료할 세션 ID
     */
    public void end(String sessionId) {
        log.info("[end] ConnectionId : {}", sessionId);

        BufferedOutputStream bos = fileStreamMap.get(sessionId);
        String filePath = filePathMap.get(sessionId);
        String metaId = metaIdMap.get(sessionId);

        if (bos == null || filePath == null) {
            log.error("[end] No open stream or file path for sessionId: {}", sessionId);
            return;
        }

        try {
            bos.flush();
            bos.close();
            
            // 파일명 추출
            String fileName = new File(filePath).getName();
            
            // 녹음 완료 이벤트 발행
            RecordCompletedEvent event = new RecordCompletedEvent(
                sessionId,
                metaId,
                filePath,
                fileName,
                LocalDateTime.now()
            );
            
            // kafka-core의 EventPublisher를 사용하여 이벤트 발행
            eventPublisher.publish(event, sessionId);
            log.info("[end] Record completed event published for sessionId: {}, fileName: {}", sessionId, fileName);
            
        } catch (IOException e) {
            log.error("[RECORD] [END] [ERR] Error closing stream: {}", e.getMessage(), e);
            throw new BusinessException(AudioServiceErrorCode.FILE_PROCESSING_ERROR);
        } finally {
            fileStreamMap.remove(sessionId);
            filePathMap.remove(sessionId);
            metaIdMap.remove(sessionId);
        }
    }
}
