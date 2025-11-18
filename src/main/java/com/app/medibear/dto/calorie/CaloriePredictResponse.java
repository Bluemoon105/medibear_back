package com.app.medibear.dto.calorie;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CaloriePredictResponse {
    @JsonProperty("predicted_calories")
    private double predicted_calories;
}

