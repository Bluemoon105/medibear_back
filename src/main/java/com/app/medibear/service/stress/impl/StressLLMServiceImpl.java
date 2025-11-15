package com.app.medibear.service.stress.impl;

import com.app.medibear.dto.StressReportDTO;
import com.app.medibear.service.stress.StressLLMService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class StressLLMServiceImpl implements StressLLMService {

    private final WebClient webClient;

    public StressLLMServiceImpl(WebClient.Builder builder,
                                @Value("${fastapi.url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public String generateCoaching(StressReportDTO dto) {
        Map<String, Object> req = new HashMap<>();
        if (dto.getSleepHours() != null)     req.put("sleepHours", dto.getSleepHours());
        if (dto.getActivityLevel() != null)  req.put("activityLevel", dto.getActivityLevel());
        if (dto.getCaffeineCups() != null)   req.put("caffeineCups", dto.getCaffeineCups());
        if (dto.getPrimaryEmotion() != null) req.put("primaryEmotion", dto.getPrimaryEmotion());
        if (dto.getComment() != null)        req.put("comment", dto.getComment());

        return webClient.post()
                .uri("/stress/report/json")                       // JSON 엔드포인트!
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(req), Map.class)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @Override
    public String forwardAudioToFastApi(MultipartFile file) {
        return webClient.post()
                .uri("/stress/audio")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", file.getResource()))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @Override
    public String chat(Map<String, Object> body) {
        return webClient.post()
                .uri("/stress/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(body), Map.class)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}