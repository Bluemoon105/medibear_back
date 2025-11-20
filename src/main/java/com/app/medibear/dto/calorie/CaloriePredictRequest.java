package com.app.medibear.dto.calorie;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor   // 기본 생성자
@AllArgsConstructor  // 모든 필드 생성자
public class CaloriePredictRequest {

    @JsonProperty("weight_kg")
    private double weight_kg;

    @JsonProperty("height_cm")
    private double height_cm;

    @JsonProperty("duration_minutes")
    private int duration_minutes;

    @JsonProperty("activity_type")
    private String activity_type;

    private double bmi;

    private String gender;


}


