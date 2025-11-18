package com.app.medibear.dto.calorie;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CalorieLogDto {

    private double weightKg;
    private double bmi;
    private String activityType;
    private int durationMinutes;
    private double caloriesBurned;
}
