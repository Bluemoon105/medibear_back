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

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyLogService {

    private final MemberRepository memberRepository;
    private final FitnessLogRepository fitnessLogRepository;
    private final FitnessReportRepository fitnessReportRepository;

    public MyLogCalorieResponse getCalorieReport(String memberId) {

        Member member = memberRepository.findByEmail(memberId);
        if (member == null) throw new RuntimeException("회원 조회 실패");

        Long memberNo = member.getMemberNo();

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<FitnessLog> logs = fitnessLogRepository.findRecentFitnessLogs(memberNo, sevenDaysAgo);

        // 🔥 프로필 데이터
        FitnessLog latest = logs.isEmpty() ? null : logs.get(0);

        MyLogCalorieResponse.ProfileDto profile = (latest == null)
            ? new MyLogCalorieResponse.ProfileDto(0.0, 0.0, 0.0)
            : new MyLogCalorieResponse.ProfileDto(
            latest.getHeightCm(),
            latest.getWeightKg(),
            latest.getBmi()
        );

        // 칼로리 그래프 데이터
        List<MyLogCalorieResponse.CalorieChartItem> calorieChart =
            logs.stream()
                .map(log -> new MyLogCalorieResponse.CalorieChartItem(
                    log.getCreatedAt().toLocalDate().toString(),
                    log.getCaloriesBurned()
                ))
                .toList();

        // 운동 표
        List<MyLogCalorieResponse.FitnessLogItem> table =
            logs.stream()
                .map(log -> new MyLogCalorieResponse.FitnessLogItem(
                    log.getCreatedAt().toLocalDate().toString(),
                    log.getActivityType(),
                    log.getDurationMinutes(),
                    log.getCaloriesBurned()
                ))
                .toList();

        List<FitnessReport> reports = fitnessReportRepository.findLatestReport(memberNo);
        String summary = reports.isEmpty()
            ? "요약 데이터가 존재하지 않습니다."
            : reports.get(0).getSummary();

        return MyLogCalorieResponse.builder()
            .profile(profile)
            .calorieChart(calorieChart)
            .fitnessLogs(table)
            .summary(summary)
            .build();

    }
}
