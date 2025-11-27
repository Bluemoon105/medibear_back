package com.app.medibear.controller;

import com.app.medibear.dto.StressReportDTO;
import com.app.medibear.service.stress.StressLLMService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/stress")
public class StressReportController {

    private final StressLLMService llmService;

    public StressReportController(StressLLMService llmService) {
        this.llmService = llmService;
    }

    // 리포트(JSON -> FastAPI /stress/report/json)
    @PostMapping("/report")
    public ResponseEntity<String> getReport(@RequestBody StressReportDTO dto) {
        return ResponseEntity.ok(llmService.generateCoaching(dto));
    }

    // (옵션) 오디오 단독 분석 -> FastAPI /stress/audio
    @PostMapping(value = "/audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAudio(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(llmService.forwardAudioToFastApi(file));
    }

    // 챗봇 -> FastAPI /stress/chat
    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(llmService.chat(body)); // ✅ 인스턴스 메서드 호출
    }

    @PostMapping("/agent/step")
    public ResponseEntity<String> agentStep(@RequestBody Map<String, Object> body) {

        String json = llmService.agentStep(body);

        // 프론트에서 바로 JSON.parse 하게 순수 문자열 그대로 내려보냄
        return ResponseEntity.ok(json);
    }
}
