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
import java.util.*;

@Service
public class SleepService {

    private final SleepMapper sleepMapper;
    private final UserService userService;
    private final WebClient webClient;

    public SleepService(
            SleepMapper sleepMapper,
            UserService userService,
            @Value("${cors.fastapi.url}") String fastApiBaseUrl
    ) {
        this.sleepMapper = sleepMapper;
        this.userService = userService;
        this.webClient = WebClient.builder()
                .baseUrl(fastApiBaseUrl)
                .build();
    }

    /** 1️⃣ 활동 데이터 저장 (하루 1회 제한) **/
    public SleepData saveInitialRecord(UserInputRequest input) {
        String userId = input.getUserId(); 
        LocalDate today = LocalDate.now();

        boolean existsToday = sleepMapper.existsTodayRecord(userId, today);
        if (existsToday) {
            throw new IllegalStateException("오늘은 이미 활동량이 등록되었습니다.");
        }

        SleepData record = new SleepData();
        record.setUserId(userId);
        record.setDate(today);
        record.setSleepHours(input.getSleepHours());
        record.setCaffeineMg(input.getCaffeineMg());
        record.setAlcoholConsumption(input.getAlcoholConsumption());
        record.setPhysicalActivityHours(input.getPhysicalActivityHours());
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());

        sleepMapper.insert(record);
        return record;
    }

    /** 2️⃣ 오늘 데이터 조회 **/
    public SleepData findTodayRecord(String userId, LocalDate date) { 
        return sleepMapper.findTodayRecord(userId, date).orElse(null);
    }

    /** 3️⃣ 피로도 예측 **/
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

        record.setPredictedSleepQuality(
                ((Number) resp.getOrDefault("predicted_sleep_quality", 0)).doubleValue()
        );
        record.setPredictedFatigueScore(
                ((Number) resp.getOrDefault("predicted_fatigue_score", 0)).doubleValue()
        );
        record.setConditionLevel((String) resp.getOrDefault("condition_level", "보통"));

        record.setUpdatedAt(LocalDateTime.now());
        sleepMapper.updateFatigue(record);
        return record;
    }

    /** 4️⃣ 개인 최적 수면시간 예측 **/
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

        record.setRecommendedSleepRange(
                (String) resp.getOrDefault("recommended_sleep_range", "7시간")
        );

        record.setUpdatedAt(LocalDateTime.now());
        sleepMapper.updateOptimal(record);
        return record;
    }

    /** 5️⃣ 최근 7일 기록 **/
    public List<SleepData> getRecentSleepHours(String userId) {
        List<SleepData> list = sleepMapper.getRecentSleepHours(userId);
        Collections.reverse(list);
        return list;
    }
}
