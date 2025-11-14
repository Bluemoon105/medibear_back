package com.app.medibear.calorie.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor   // 기본 생성자
@AllArgsConstructor  // 모든 필드 생성자
public class CaloriePredictRequest {


    
    private double weight_kg;

    
    private double bmi;

    
    private String activity_type;

    
    private double duration_minutes;


}


