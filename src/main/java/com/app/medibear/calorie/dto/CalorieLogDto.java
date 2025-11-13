package com.app.medibear.calorie.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalorieLogDto {
    private double weightKg;
    private double bmi;
    private String activityType;
    private int durationMinutes;
    private double caloriesBurned;
}
