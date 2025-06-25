package com.kr.audio_server.controller;

import com.kr.audio_server.dto.EndRequest;
import com.kr.audio_server.dto.IdleRequest;
import com.kr.audio_server.dto.RecordRequest;
import com.kr.audio_server.service.RecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/record")
public class RecordController {

    private final RecordService recordService;

    RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping("/start")
    public void start(@RequestBody RecordRequest dto) {
        recordService.start(dto);
    }

    @PostMapping("/idle")
    public void idle(@RequestBody IdleRequest dto) {
        recordService.saveChunk(dto);
    }

    @PostMapping("/end")
    public void end(@RequestBody EndRequest dto) {
        recordService.end(dto.getSessionId());
    }
}
