package com.app.medibear.service;

import com.app.medibear.dto.UserInputRequest;
import com.app.medibear.mapper.SleepMapper;
import com.app.medibear.model.SleepData;
import com.app.medibear.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class SleepService {

    private final SleepMapper sleepMapper;
    private final UserService userService;
    private final WebClient webClient;

    public SleepService(
            SleepMapper sleepMapper,
            UserService userService,
            @Value("${fastapi.base-url}") String fastApiBaseUrl
    ) {
        this.sleepMapper = sleepMapper;
        this.userService = userService;
        this.webClient = WebClient.builder()
                .baseUrl(fastApiBaseUrl)
                .build();
    }

    /** 1) 1차 저장 **/
    public SleepData saveInitialRecord(UserInputRequest input) {
        SleepData record = new SleepData();
        record.setUserId(input.getUserId());
        record.setDate(LocalDate.now());
        record.setSleepHours(input.getSleepHours());
        record.setCaffeineMg(input.getCaffeineMg());
        record.setAlcoholConsumption(input.getAlcoholConsumption());
        record.setPhysicalActivityHours(input.getPhysicalActivityHours());
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());

        sleepMapper.insert(record);
        return record;
    }

    /** 2) 피로도 예측 **/
    public SleepData updateFatiguePrediction(SleepData record) {
        User user = userService.getUserById(record.getUserId());
        int age = userService.calculateAge(user.getBirthDate());
        int genderInt = userService.toGenderInt(user.getGender());

        Map<String, Object> body = Map.of(
                "age", age,
                "gender", genderInt,
                "caffeine_mg", record.getCaffeineMg(),
                "sleep_hours", record.getSleepHours(),
                "physical_activity_hours", record.getPhysicalActivityHours(),
                "alcohol_consumption", record.getAlcoholConsumption()
        );

        Map<String, Object> resp = webClient.post()
                .uri("/sleep/predict-fatigue")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        if (resp == null) throw new IllegalStateException("FastAPI returned null");

        if (resp.get("predicted_sleep_quality") != null)
            record.setPredictedSleepQuality(((Number) resp.get("predicted_sleep_quality")).doubleValue());
        if (resp.get("predicted_fatigue_score") != null)
            record.setPredictedFatigueScore(((Number) resp.get("predicted_fatigue_score")).doubleValue());
        if (resp.get("condition_level") != null)
            record.setConditionLevel((String) resp.get("condition_level"));

        record.setUpdatedAt(LocalDateTime.now());
        sleepMapper.updateFatigue(record);
        return record;
    }

    /** 3) 개인 최적 수면시간 예측 **/
    public SleepData updateOptimalSleepRange(SleepData record) {
        User user = userService.getUserById(record.getUserId());
        int age = userService.calculateAge(user.getBirthDate());
        int genderInt = userService.toGenderInt(user.getGender());

        Map<String, Object> body = Map.of(
                "age", age,
                "gender", genderInt,
                "caffeine_mg", record.getCaffeineMg(),
                "sleep_hours", record.getSleepHours(),
                "alcohol_consumption", record.getAlcoholConsumption(),
                "physical_activity_hours", record.getPhysicalActivityHours()
        );

        Map<String, Object> resp = webClient.post()
                .uri("/sleep/recommend")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        if (resp == null) throw new IllegalStateException("FastAPI returned null");

        if (resp.get("recommended_sleep_range") != null)
            record.setRecommendedSleepRange((String) resp.get("recommended_sleep_range"));

        record.setUpdatedAt(LocalDateTime.now());
        sleepMapper.updateOptimal(record);
        return record;
    }

    /** 4) ID로 조회 **/
    public SleepData findById(Long id) {
        return sleepMapper.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Record not found: " + id));
    }
}