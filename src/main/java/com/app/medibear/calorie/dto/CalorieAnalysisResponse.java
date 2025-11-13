package com.app.medibear.calorie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalorieAnalysisResponse {
    private String prompt;
    private String advice;

}
