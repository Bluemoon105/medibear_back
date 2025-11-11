package com.app.medibear.controller;

import com.app.medibear.service.LLMService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chat")
public class SleepChatController {

    private final LLMService llmService;

    public SleepChatController(LLMService llmService) {
        this.llmService = llmService;
    }

    /**
     * 일반 대화 (LLM)
     * POST /chat/message
     */
    @PostMapping("/message")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, Object> body) {
        String userId = (String) body.get("user_id"); // ✅ String으로 변경
        String message = (String) body.get("message");

        String response = llmService.chatGeneral(userId, message); // ✅ LLMService도 String으로 맞춰야 함
        return ResponseEntity.ok(Map.of("response", response));
    }

    /**
     * 일간 리포트
     * GET /chat/report/daily/{userId}
     */
    @GetMapping("/report/daily/{userId}")
    public ResponseEntity<Map<String, Object>> dailyReport(@PathVariable("userId") String userId) {
        String report = llmService.getDailyReport(userId);
        return ResponseEntity.ok(Map.of("report", report));
    }

    /**
     * 주간 리포트
     * GET /chat/report/weekly/{userId}
     */
    @GetMapping("/report/weekly/{userId}")
    public ResponseEntity<Map<String, Object>> weeklyReport(@PathVariable("userId") String userId) { 
        String report = llmService.getWeeklyReport(userId);
        return ResponseEntity.ok(Map.of("report", report));
    }

    /**
     * 대화 기록 조회
     * GET /chat/history/{userId}
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<Map<String, Object>> history(@PathVariable("userId") String userId) { 
        Map<String, Object> history = llmService.getChatHistory(userId);
        return ResponseEntity.ok(history);
    }
}
