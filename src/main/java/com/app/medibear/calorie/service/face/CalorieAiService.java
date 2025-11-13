package com.app.medibear.calorie.service.face;

import com.app.medibear.calorie.dto.CalorieAnalysisResponse;
import com.app.medibear.calorie.dto.CaloriePredictRequest;
import com.app.medibear.calorie.dto.CaloriePredictResponse;
import reactor.core.publisher.Mono;

public interface CalorieAiService {

    Mono<CaloriePredictResponse> getCaloriePrediction(CaloriePredictRequest caloriePredictRequest);

    Mono<CalorieAnalysisResponse> getCalorieAnalyze();
}
