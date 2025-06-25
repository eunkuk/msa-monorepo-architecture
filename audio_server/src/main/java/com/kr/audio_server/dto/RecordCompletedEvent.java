package com.kr.audio_server.dto;

import com.kr.kafka.config.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 오디오 녹음 완료 이벤트
 * 녹음이 완료되었을 때 발행되는 카프카 이벤트
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordCompletedEvent implements Event {
    
    private String sessionId;
    private String metaId;
    private String filePath;
    private String fileName;
    private LocalDateTime completedAt;
    
    @Override
    public String getTopic() {
        return "audio.record.completed";
    }
    
    @Override
    public LocalDateTime getTimestamp() {
        return completedAt != null ? completedAt : LocalDateTime.now();
    }
    
} 