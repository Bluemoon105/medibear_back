package com.app.medibear.controller;

import com.app.medibear.service.SleepLLMService;
import com.app.medibear.utils.GetMemberId;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class SleepChatController {

    private final SleepLLMService llmService;
    private final GetMemberId getMemberId;

//    public SleepChatController(SleepLLMService llmService) {
//        this.llmService = llmService;
//    }

    /** 일반 대화 */
    @PostMapping("/message")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, Object> body, HttpServletRequest request) {

        String email = body.get("email").toString();
        String message = body.get("message").toString();

        String response = llmService.chatGeneral(email, message);
        return ResponseEntity.ok(Map.of("response", response));
    }

    /** 일간 리포트 */
    @GetMapping("/report/daily")
    public ResponseEntity<Map<String, Object>> dailyReport(@RequestParam ("email") String email, HttpServletRequest request) {
    	String authorizationHeader = request.getHeader("Authorization");
        String memberId =  getMemberId.getMemberId(authorizationHeader);
    	String report = llmService.getDailyReport(email);
        return ResponseEntity.ok(Map.of("report", report));
    }

    /** 주간 리포트 */
    @GetMapping("/report/weekly")
    public ResponseEntity<Map<String, Object>> weeklyReport(@RequestParam ("email") String email, HttpServletRequest request) {
    	String authorizationHeader = request.getHeader("Authorization");
        String memberId =  getMemberId.getMemberId(authorizationHeader);
    	String report = llmService.getWeeklyReport(email);
        return ResponseEntity.ok(Map.of("report", report));
    }
}
