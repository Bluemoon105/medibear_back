package com.app.medibear.service.stress.impl;

import com.app.medibear.dto.StressReportDTO;
import com.app.medibear.service.stress.StressLLMService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

@Service
public class StressLLMServiceImpl implements StressLLMService {

    private final RestTemplate restTemplate;
    private final String fastApiBaseUrl;

    // application.yml / properties 에서 fastapi.url = http://127.0.0.1:8000 이런 식으로 세팅
    public StressLLMServiceImpl(
            RestTemplate restTemplate,
            @Value("${fastapi.url}") String fastApiBaseUrl
    ) {
        this.restTemplate = restTemplate;
        // 마지막 슬래시(/) 중복 방지용으로 정리
        if (fastApiBaseUrl.endsWith("/")) {
            this.fastApiBaseUrl = fastApiBaseUrl.substring(0, fastApiBaseUrl.length() - 1);
        } else {
            this.fastApiBaseUrl = fastApiBaseUrl;
        }
    }

    /**
     * React -> Spring(/api/stress/report) -> FastAPI(/stress/report/json)
     * ML + DL + LLM 기반 통합 리포트 호출
     */
    @Override
    public String generateCoaching(StressReportDTO dto) {

        // ✅ FastAPI 라우트와 일치
        String url = fastApiBaseUrl + "/stress/report/json";

        // ✅ JSON body (ReportIn 스키마에 맞게)
        Map<String, Object> body = new HashMap<>();

        if (dto.getSleepHours() != null) {
            body.put("sleepHours", dto.getSleepHours());
        }
        if (dto.getActivityLevel() != null) {
            body.put("activityLevel", dto.getActivityLevel());
        }
        if (dto.getCaffeineCups() != null) {
            body.put("caffeineCups", dto.getCaffeineCups());
        }
        if (dto.getPrimaryEmotion() != null) {
            body.put("primaryEmotion", dto.getPrimaryEmotion());
        }
        if (dto.getComment() != null) {
            body.put("comment", dto.getComment());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(url, entity, String.class);

        return response.getBody();
    }

    /**
     * 음성 파일 단독 분석 -> FastAPI /stress/audio
     */
    @Override
    public String forwardAudioToFastApi(MultipartFile file) {

        String url = fastApiBaseUrl + "/stress/audio";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());  // FastAPI 라우터에서 "file" 로 받아야 함

        HttpEntity<MultiValueMap<String, Object>> requestEntity =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(url, requestEntity, String.class);

        return response.getBody();
    }

    /**
     * 챗봇 질의 -> FastAPI /stress/chat
     */
    @Override
    public String chat(Map<String, Object> bodyMap) {

        String url = fastApiBaseUrl + "/stress/chat";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(bodyMap, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(url, entity, String.class);

        return response.getBody();
    }

    @Override
    public String agentStep(Map<String, Object> bodyMap) {

        String url = fastApiBaseUrl + "/stress/agent/step";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(bodyMap, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(url, entity, String.class);

        return response.getBody();
    }
}
