package com.app.medibear.controller;

import com.app.medibear.service.SleepLLMService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chat")
public class SleepChatController {

    private final SleepLLMService llmService;

    public SleepChatController(SleepLLMService llmService) {
        this.llmService = llmService;
    }

    /** 일반 대화 */
    @PostMapping("/message")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, Object> body) {

        String email = body.get("email").toString();
        String message = body.get("message").toString();

        String response = llmService.chatGeneral(email, message);
        return ResponseEntity.ok(Map.of("response", response));
    }

    /** 일간 리포트 */
    @GetMapping("/report/daily/{email}")
    public ResponseEntity<Map<String, Object>> dailyReport(@PathVariable String email) {
        String report = llmService.getDailyReport(email);
        return ResponseEntity.ok(Map.of("report", report));
    }

    /** 주간 리포트 */
    @GetMapping("/report/weekly/{email}")
    public ResponseEntity<Map<String, Object>> weeklyReport(@PathVariable String email) {
        String report = llmService.getWeeklyReport(email);
        return ResponseEntity.ok(Map.of("report", report));
    }

    /** 대화 기록 조회 */
    @GetMapping("/history/{email}")
    public ResponseEntity<Map<String, Object>> history(@PathVariable String email) {
        Map<String, Object> history = llmService.getChatHistory(email);
        return ResponseEntity.ok(history);
    }
}
