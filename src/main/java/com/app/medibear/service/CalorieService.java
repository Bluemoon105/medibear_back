package com.app.medibear.service;

import com.app.medibear.dto.calorie.*;
import com.app.medibear.entity.FitnessReport;
import com.app.medibear.entity.Member;
import com.app.medibear.entity.FitnessLog;
import com.app.medibear.repository.FitnessLogRepository;
import com.app.medibear.repository.FitnessReportRepository;
import com.app.medibear.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
@Slf4j
public class CalorieService{

    private final RestTemplate restTemplate;
    private final FitnessLogRepository fitnessLogRepository;
    private final MemberRepository memberRepository;
    private final FitnessReportRepository fitnessReportRepository;


    @Value("${fastapi.url}")
    private String fastapiUrl;
    /**
     * 칼로리 소모량 예측값 요청
     * @param calorieRequest - 몸무게, bmi, 운동 종류, 운동시간
     * @return 칼로리 소모량 예측값
     */
    public CaloriePredictResponse getCaloriePrediction(
        CaloriePredictRequest calorieRequest,
        String memberId) {

        String url = fastapiUrl + "/calorie/predict";

        Member member = memberRepository.findByEmail(memberId);
        if (member == null) {
            throw new RuntimeException("Member 조회 실패 → ID: " + memberId);
        }
        // gender 추가 (Enum → "M"/"F")
        calorieRequest.setGender(member.getGender().name());

        // BMI 직접 계산
        double heightM = calorieRequest.getHeight_cm() / 100.0;
        double bmi = calorieRequest.getWeight_kg() / (heightM * heightM);
        calorieRequest.setBmi(bmi);

        // fastAPI 전달
        ResponseEntity<CaloriePredictResponse> responseEntity =
            restTemplate.postForEntity(
                url,
                calorieRequest,
                CaloriePredictResponse.class
            );

        CaloriePredictResponse response = responseEntity.getBody();

        if (response == null) {
            throw new RuntimeException("FastAPI로 부터 빈 응답이 옴");
        }

        LocalDateTime now = LocalDateTime.now();

        // 🔥 FitnessLog 저장
        FitnessLog log = FitnessLog.builder()
            .member(member)
            .activityType(calorieRequest.getActivity_type())
            .durationMinutes(calorieRequest.getDuration_minutes())
            .caloriesBurned(response.getPredicted_calories())
            .weightKg(calorieRequest.getWeight_kg())
            .heightCm(calorieRequest.getHeight_cm())
            .bmi(bmi)
            .createdAt(now)
            .updatedAt(now)
            .build();

        fitnessLogRepository.save(log);

        return response;
    }

    /**
     * 사용자의 최근 30일 운동 기록 데이터로 LLM에 분석 프롬프트 요청
     * @return 분석/예측 프롬프트
     */
    public CalorieAnalysisResponse getCalorieAnalyze(String memberId) {

        // 최근 7일 기준 날짜 계산
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);

        // 사용자 조회
        Member member = memberRepository.findByEmail(memberId);
        if (member == null) {
            throw new RuntimeException("회원 정보를 찾을 수 없습니다: " + memberId);
        }

        Long memberNo = member.getMemberNo();
        log.info("service memberNo: {}", memberNo);

        // 최근 7일 운동 로그 조회
        List<FitnessLog> logs = fitnessLogRepository.findRecentFitnessLogs(memberNo, weekAgo);

        // 최신 FitnessLog 조회
        FitnessLog latest = fitnessLogRepository.findTopByMember_MemberNoOrderByCreatedAtDesc(memberNo);

        if (latest == null) {
            throw new RuntimeException("최신 FitnessLog 데이터가 없습니다.");
        }

        // FitnessLog → CalorieLogDto 변환
        List<CalorieLogDto> calorieLog = logs.stream()
            .map(log -> new CalorieLogDto(
                log.getWeightKg(),
                log.getBmi(),
                log.getActivityType(),
                log.getDurationMinutes(),
                log.getCaloriesBurned()
            ))
            .toList();

        // FastAPI
        CalorieAnalyzeRequest request = new CalorieAnalyzeRequest(
            new CalorieAnalyzeRequest.MemberInfo(
                member.getName(),
                member.getGender().name()
            ),
            new CalorieAnalyzeRequest.LatestFitnessInfo(
                latest.getHeightCm(),
                latest.getWeightKg(),
                latest.getBmi()
            ),
            calorieLog
        );

        // FastAPI URI
        String url = fastapiUrl + "/calorie/llm/analyze";

        // FastAPI에 POST 요청
        ResponseEntity<CalorieAnalysisResponse> responseEntity =
            restTemplate.postForEntity(
                url,
                request,
                CalorieAnalysisResponse.class
            );

        CalorieAnalysisResponse response = responseEntity.getBody();

        if (response == null) {
            throw new RuntimeException("FastAPI analyze 응답이 null 입니다.");
        }

        FitnessReport report = FitnessReport.builder()
            .member(member)
            .summary(response.getSummary())   // 요약 텍스트
            .advice(response.getAdvice())     // 전체 분석 텍스트
            .createdAt(LocalDateTime.now())
            .build();

        fitnessReportRepository.save(report);

        // 🔥 저장 후 그대로 반환 (리액트에서 상세 분석 사용)
        return response;
    }
}
