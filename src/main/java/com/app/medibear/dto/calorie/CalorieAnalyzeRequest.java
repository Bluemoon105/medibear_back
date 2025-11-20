package com.app.medibear.dto.calorie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalorieAnalyzeRequest {

    private MemberInfo member;
    private LatestFitnessInfo latest;
    private List<CalorieLogDto> logs;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberInfo {
        private String name;
        private String gender;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LatestFitnessInfo {
        private Double heightCm;
        private Double weightKg;
        private Double bmi;
    }
}
