package com.app.medibear.calorie.service.impl;

import com.app.medibear.calorie.dao.CalorieAiDao;
import com.app.medibear.calorie.dto.CalorieAnalysisResponse;
import com.app.medibear.calorie.dto.CalorieLogDto;
import com.app.medibear.calorie.dto.CaloriePredictRequest;
import com.app.medibear.calorie.dto.CaloriePredictResponse;
import com.app.medibear.calorie.service.face.CalorieAiService;
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

public class CalorieAiServiceImpl implements CalorieAiService {

    private CalorieAiDao calorieAiDao;
    private final WebClient webClient;

    /**
     * 칼로리 소모량 예측값 요청
     * @param caloriePredictRequest - 몸무게, bmi, 운동 종류, 운동시간
     * @return 칼로리 소모량 예측값
     */
    @Override
    public Mono<CaloriePredictResponse> getCaloriePrediction(CaloriePredictRequest caloriePredictRequest) {

        // 1.받은 데이터 db 저장
        // calorieAiDao.insertWorkoutDataByMemberId(caloriePredictRequest, memberId)

        return webClient.post()
            .uri("/calorie/predict")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(caloriePredictRequest)
            .retrieve()
            .bodyToMono(CaloriePredictResponse.class);
    }

    /**
     * 사용자의 최근 30일 운동 기록 데이터로 LLM에 분석 프롬프트 요청
     * @return 분석/예측 프롬프트
     */
    @Override
    public Mono<CalorieAnalysisResponse> getCalorieAnalyze() {
        // memberNo를 통해 db에서 조회한 최근 30일 동안의 기록을 조회
        // List<renameCalorieLogDto> logs = calorieDao.findRecentWorkoutLogs(memberId);
        List<CalorieLogDto> logs = new ArrayList<>();

        logs.add(new CalorieLogDto(68.8, 34.9,"Cycling", 50,480.5));
        logs.add(new CalorieLogDto(68.6, 34.7,"Cycling", 66,600.0));
        logs.add(new CalorieLogDto(68.5, 34.6,"Cycling" ,80, 514.1));
        logs.add(new CalorieLogDto(68.6, 34.6,"Tennis", 30,300.0));

        // FastAPI로 기록을 통해 프롬프트 생성 요청
        return webClient.post()
            .uri("/calorie/llm/analyze")
            .bodyValue(logs)
            .retrieve()
            .bodyToMono(CalorieAnalysisResponse.class);
    }
}
