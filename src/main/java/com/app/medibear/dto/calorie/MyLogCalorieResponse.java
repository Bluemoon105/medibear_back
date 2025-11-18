package com.app.medibear.dto.calorie;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyLogCalorieResponse {

    private ProfileDto profile;                    // 1) 신체 정보
    private List<CalorieChartItem> calorieChart;   // 2) 7일 그래프
    private List<FitnessLogItem> fitnessLogs;      // 3) 운동 기록 표
    private String summary;                        // 3) 7일 분석 요약

    // ----------------------------
    // 🔸 신체 정보
    // ----------------------------
    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class ProfileDto {
        private Double heightCm;
        private Double weightKg;
        private Double bmi;
    }

    // ----------------------------
    // 🔸 칼로리 그래프용
    // ----------------------------
    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class CalorieChartItem {
        private String date;      // yyyy-MM-dd
        private Double calories;
    }

    // ----------------------------
    // 🔸 운동 기록 표
    // ----------------------------
    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class FitnessLogItem {
        private String date;
        private String activityType;
        private Integer durationMinutes;
        private Double caloriesBurned;
    }
}

