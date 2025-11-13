package com.app.medibear.calorie.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor   // 기본 생성자
@AllArgsConstructor  // 모든 필드 생성자
public class CaloriePredictRequest {


    @Positive(message = "weight_kg는 양수여야 합니다.")
    private double weight_kg;

    @Positive(message = "bmi는 양수여야 합니다.")
    private double bmi;

    @NotBlank(message = "activity_type은 필수입니다.")
    private String activity_type;

    @Positive(message = "duration_minutes는 양수여야 합니다.")
    private double duration_minutes;


}


