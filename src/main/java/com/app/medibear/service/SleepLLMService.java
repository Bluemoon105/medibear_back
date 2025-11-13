package com.app.medibear.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class SleepLLMService {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public SleepLLMService(@Value("${fastapi.url}") String baseUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
    }

    /** 메시지 대화 (email 기반) */
    public String chatGeneral(String email, String message) {
        String url = baseUrl + "/sleepchat/message";

        Map<String, Object> body = Map.of(
                "email", email,
                "message", message
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getBody() == null || response.getBody().get("response") == null)
            return "LLM 응답을 가져오지 못했습니다.";

        return response.getBody().get("response").toString();
    }

    /** 일간 리포트 요청 */
    public String getDailyReport(String email) {
        String url = baseUrl + "/sleepchat/report/daily/" + email;

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        if (response.getBody() == null || response.getBody().get("report") == null)
            return "일간 리포트를 가져오지 못했습니다.";

        return response.getBody().get("report").toString();
    }

    /** 주간 리포트 요청 */
    public String getWeeklyReport(String email) {
        String url = baseUrl + "/sleepchat/report/weekly/" + email;

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        if (response.getBody() == null || response.getBody().get("report") == null)
            return "주간 리포트를 가져오지 못했습니다.";

        return response.getBody().get("report").toString();
    }

    /** 대화 기록 조회 */
    public Map<String, Object> getChatHistory(String email) {
        String url = baseUrl + "/sleepchat/history/" + email;

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        if (response.getBody() == null)
            return Map.of("error", "대화 기록을 가져오지 못했습니다.");

        return response.getBody();
    }
}
