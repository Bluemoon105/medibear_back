package com.app.medibear.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class LLMService {

    private final WebClient webClient;

    public LLMService(@Value("${cors.fastapi.url}") String baseUrl, WebClient.Builder builder) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    //일반 대화
    public String chatGeneral(Long userId, String message) {
        Map<String, Object> body = Map.of(
            "user_id", userId,
            "message", message
        );

        Map<String, Object> resp = webClient.post()
                .uri("/sleepchat/message")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        if (resp == null || resp.get("response") == null)
            return "LLM 응답을 가져오지 못했습니다.";

        return (String) resp.get("response");
    }

    //일간 리포트
    public String getDailyReport(Long userId) {
        Map<String, Object> resp = webClient.get()
                .uri("/sleepchat/report/daily/" + userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        if (resp == null || resp.get("report") == null)
            return "일간 리포트를 가져오지 못했습니다.";

        return (String) resp.get("report");
    }

    //주간 리포트
    public String getWeeklyReport(Long userId) {
        Map<String, Object> resp = webClient.get()
                .uri("/sleepchat/report/weekly/" + userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        if (resp == null || resp.get("report") == null)
            return "주간 리포트를 가져오지 못했습니다.";

        return (String) resp.get("report");
    }

    //대화 기록
    public Map<String, Object> getChatHistory(Long userId) {
        return webClient.get()
                .uri("/sleepchat/history/" + userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }
}