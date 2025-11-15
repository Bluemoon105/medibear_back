package com.app.medibear.service;

import com.app.medibear.calorie.dto.CalorieAnalysisResponse;
import com.app.medibear.dto.calorie.CalorieLogDto;
import com.app.medibear.calorie.dto.CaloriePredictRequest;
import com.app.medibear.calorie.dto.CaloriePredictResponse;
import com.app.medibear.entity.Member;
import com.app.medibear.entity.WorkoutLog;
import com.app.medibear.repository.CalorieRepository;
import com.app.medibear.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



@Service
@RequiredArgsConstructor
public class CalorieService{

    private final WebClient webClient;
    private final CalorieRepository calorieRepository;
    private final MemberRepository memberRepository;

    /**
     * 칼로리 소모량 예측값 요청
     * @param caloriePredictRequest - 몸무게, bmi, 운동 종류, 운동시간
     * @return 칼로리 소모량 예측값
     */
    public Mono<CaloriePredictResponse> getCaloriePrediction(CaloriePredictRequest caloriePredictRequest, Long memberId) {

        return webClient.post()
            .uri("/calorie/predict")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(caloriePredictRequest)
            .retrieve()
            .bodyToMono(CaloriePredictResponse.class)
            .map(response -> {
                // memberId로 Member 조회
                Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("Member not found"));

                // DTO -> 엔티티 변환
                WorkoutLog workoutLog = WorkoutLog.builder()
                    .member(member)
                    .activityType(caloriePredictRequest.getActivity_type())
                    .durationMinutes(caloriePredictRequest.getDuration_minutes())
                    .caloriesBurned(response.getPredicted_calories()) // 에측값 저장
                    .createdAt(LocalDateTime.now())
                    .build();

                calorieRepository.save(workoutLog);
                return response;
            });
    }

    /**
     * 사용자의 최근 30일 운동 기록 데이터로 LLM에 분석 프롬프트 요청
     * @return 분석/예측 프롬프트
     */

    public Mono<CalorieAnalysisResponse> getCalorieAnalyze() {
        List<CalorieLogDto> logs = new ArrayList<>();
        // 1. 일주일동안의 데이터 조회


        logs.add(new CalorieLogDto(68.8, 34.9,"Cycling", 50,480.5));
        logs.add(new CalorieLogDto(68.6, 34.7,"Cycling", 66,600.0));
        logs.add(new CalorieLogDto(68.5, 34.6,"Cycling" ,80, 514.1));
        logs.add(new CalorieLogDto(68.6, 34.6,"Tennis", 30,300.0));
        // 2. 조회한 데이터를 fastAPI에 전송 후 프롬프트 생성 요청

        // 프롬프트 생성
        return webClient.post()
            .uri("/calorie/llm/analyze")
            .bodyValue(logs)
            .retrieve()
            .bodyToMono(CalorieAnalysisResponse.class);
    }
}
