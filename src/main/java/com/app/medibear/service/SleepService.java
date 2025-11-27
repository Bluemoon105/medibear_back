package com.app.medibear.service;

import com.app.medibear.dto.UserInputRequest;
import com.app.medibear.model.SleepData;
import com.app.medibear.model.User;
import com.app.medibear.repository.SleepDataRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class SleepService {

    private final SleepDataRepository sleepDataRepository;
    private final UserService userService;
    private final RestTemplate restTemplate;
    private final String fastApiBaseUrl;

    public SleepService(
            SleepDataRepository sleepDataRepository,
            UserService userService,
            @Value("${fastapi.url}") String fastApiBaseUrl
    ) {
        this.sleepDataRepository = sleepDataRepository;
        this.userService = userService;
        this.restTemplate = new RestTemplate();
        this.fastApiBaseUrl = fastApiBaseUrl;
    }

    /** 활동량 최초 저장 (email → memberNo 변환) */
    public SleepData saveInitialRecord(UserInputRequest input) {

        User user = userService.getUserByEmail(input.getEmail());
        Long memberNo = user.getMemberNo();
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        if (sleepDataRepository.existsByMemberNoAndDate(memberNo, today)) {
            throw new IllegalStateException("오늘 기록은 이미 존재합니다.");
        }

        SleepData record = new SleepData();
        record.setMemberNo(memberNo);
        record.setDate(today);
        record.setSleepHours(input.getSleepHours());
        record.setCaffeineMg(input.getCaffeineMg());
        record.setAlcoholConsumption(input.getAlcoholConsumption());
        record.setPhysicalActivityHours(input.getPhysicalActivityHours());
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());

        return sleepDataRepository.save(record);
    }

    /** 오늘 기록 찾기 (email → memberNo) */
    public SleepData findTodayRecord(String email, LocalDate date) {

        User user = userService.getUserByEmail(email);
        Long memberNo = user.getMemberNo();

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        return sleepDataRepository.findByMemberNoAndDate(memberNo, today)
                .orElse(null);
    }

    /** 피로도 예측 */
    public SleepData updateFatiguePrediction(SleepData record) {

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        if (!record.getDate().equals(today)) {
            throw new IllegalStateException("오늘 데이터가 아니므로 피로도 예측을 수행할 수 없습니다.");
        }

        User user = userService.getUserByMemberNo(record.getMemberNo());
        int age = userService.calculateAge(user.getBirthDate());
        int gender = userService.toGenderInt(user.getGender());

        Map<String, Object> body = Map.of(
                "age", age,
                "gender", gender,
                "caffeine_mg", record.getCaffeineMg(),
                "sleep_hours", record.getSleepHours(),
                "physical_activity_hours", record.getPhysicalActivityHours(),
                "alcohol_consumption", record.getAlcoholConsumption()
        );

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        fastApiBaseUrl + "/sleep/predict-fatigue",
                        body, Map.class
                );

        Map<String, Object> resp = response.getBody();
        if (resp == null) throw new IllegalStateException("FastAPI 응답 없음");

        record.setPredictedSleepQuality(((Number) resp.get("predicted_sleep_quality")).doubleValue());
        record.setPredictedFatigueScore(((Number) resp.get("predicted_fatigue_score")).doubleValue());
        record.setConditionLevel((String) resp.getOrDefault("condition_level", "보통"));
        record.setUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        
        return sleepDataRepository.save(record);
    }

    /** 최적 수면시간 예측 */
    public SleepData updateOptimalSleepRange(SleepData record) {
        
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        if (!record.getDate().equals(today)) {
            throw new IllegalStateException("오늘 데이터가 아니므로 피로도 예측을 수행할 수 없습니다.");
        }

        User user = userService.getUserByMemberNo(record.getMemberNo());
        int age = userService.calculateAge(user.getBirthDate());
        int gender = userService.toGenderInt(user.getGender());

        Map<String, Object> body = Map.of(
                "age", age,
                "gender", gender,
                "caffeine_mg", record.getCaffeineMg(),
                "sleep_hours", record.getSleepHours(),
                "alcohol_consumption", record.getAlcoholConsumption(),
                "physical_activity_hours", record.getPhysicalActivityHours()
        );

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        fastApiBaseUrl + "/sleep/recommend",
                        body, Map.class
                );

        Map<String, Object> resp = response.getBody();
        if (resp == null) throw new IllegalStateException("FastAPI 응답 없음");

        record.setRecommendedSleepRange((String) resp.get("recommended_sleep_range"));
        record.setUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")));

        return sleepDataRepository.save(record);
    }

    /** 최근 7일 데이터 (email → memberNo 변환) */
    public List<SleepData> getRecentSleepHours(String email) {

        User user = userService.getUserByEmail(email);
        Long memberNo = user.getMemberNo();

        List<SleepData> list =
                sleepDataRepository.findTop7ByMemberNoOrderByDateDesc(memberNo);

        Collections.reverse(list);
        return list;
    }
}


