package com.app.medibear.service;

import com.app.medibear.dto.calorie.MyLogCalorieResponse;
import com.app.medibear.entity.FitnessLog;
import com.app.medibear.entity.FitnessReport;
import com.app.medibear.entity.Member;
import com.app.medibear.repository.FitnessLogRepository;
import com.app.medibear.repository.FitnessReportRepository;
import com.app.medibear.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyLogService {

    private final MemberRepository memberRepository;
    private final FitnessLogRepository fitnessLogRepository;
    private final FitnessReportRepository fitnessReportRepository;

    public MyLogCalorieResponse getCalorieReport(String memberId) {

        // 회원 조회
        Member member = memberRepository.findByEmail(memberId);
        if (member == null) throw new RuntimeException("회원 조회 실패");

        Long memberNo = member.getMemberNo();

        // 최근 7일 기준 날짜
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        // 최근 7일 운동 로그 전체 조회
        List<FitnessLog> logs = fitnessLogRepository.findRecentFitnessLogs(memberNo, sevenDaysAgo);

        // 날짜별 그룹핑 (여러 번 운동한 날 합산)
        Map<LocalDate, List<FitnessLog>> groupedByDate =
            logs.stream().collect(
                Collectors.groupingBy(log -> log.getCreatedAt().toLocalDate())
            );

        // 프로필 데이터: 최근 운동 기록 기준
        FitnessLog latest = logs.isEmpty() ? null : logs.get(0);

        MyLogCalorieResponse.ProfileDto profile = (latest == null)
            ? new MyLogCalorieResponse.ProfileDto(0.0, 0.0, 0.0)
            : new MyLogCalorieResponse.ProfileDto(
            latest.getHeightCm(),
            latest.getWeightKg(),
            latest.getBmi()
        );

        // 칼로리 그래프 데이터 (날짜별 합산)
        List<MyLogCalorieResponse.CalorieChartItem> calorieChart =
            groupedByDate.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<FitnessLog> dayLogs = entry.getValue();

                    double totalCalories = dayLogs.stream()
                        .mapToDouble(FitnessLog::getCaloriesBurned)
                        .sum();

                    return new MyLogCalorieResponse.CalorieChartItem(
                        date.toString(),
                        totalCalories
                    );
                })
                .sorted(Comparator.comparing(MyLogCalorieResponse.CalorieChartItem::getDate).reversed())
                .toList();

        // 운동 표 (날짜별 합산: 총합 1줄 표기)
        List<MyLogCalorieResponse.FitnessLogItem> table =
            groupedByDate.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<FitnessLog> dayLogs = entry.getValue();

                    int totalDuration = dayLogs.stream()
                        .mapToInt(FitnessLog::getDurationMinutes)
                        .sum();

                    double totalCalories = dayLogs.stream()
                        .mapToDouble(FitnessLog::getCaloriesBurned)
                        .sum();

                    return new MyLogCalorieResponse.FitnessLogItem(
                        date.toString(),
                        "총합",
                        totalDuration,
                        totalCalories
                    );
                })
                .sorted(Comparator.comparing(MyLogCalorieResponse.FitnessLogItem::getDate).reversed())
                .toList();

        //최근 분석 보고서
        List<FitnessReport> reports = fitnessReportRepository.findLatestReport(memberNo);

        String summary = reports.isEmpty()
            ? "요약 데이터가 존재하지 않습니다."
            : reports.get(0).getSummary();

        // 🔥 최종 응답 객체 생성
        return MyLogCalorieResponse.builder()
            .profile(profile)
            .calorieChart(calorieChart)
            .fitnessLogs(table)
            .summary(summary)
            .build();
    }
}

