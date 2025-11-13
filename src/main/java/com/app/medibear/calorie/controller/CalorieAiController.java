package com.app.medibear.calorie.controller;

import com.app.medibear.calorie.dto.CalorieAnalysisResponse;
import com.app.medibear.calorie.dto.CaloriePredictRequest;
import com.app.medibear.calorie.dto.CaloriePredictResponse;
import com.app.medibear.calorie.service.face.CalorieAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai/calorie/")
public class CalorieAiController {
    private final CalorieAiService calorieAiService;
    // 클라이언트 -> 서버 : Request
    // 서버 -> 클라이언트 : Response

    @RequestMapping(value= "/predict", method = RequestMethod.POST)
    public Mono<CaloriePredictResponse> caloriePrediction(@RequestBody CaloriePredictRequest caloriePredictRequest) {
        // 클라이언트에서 받아야 하는 정보 : workout, memberId
        //
        return calorieAiService.getCaloriePrediction(caloriePredictRequest);
    }

    @RequestMapping(value="/analyze", method = RequestMethod.POST)
    public Mono<CalorieAnalysisResponse> calorieLLMAnalyze() {
        // 받아야 할 데이터 : memberId
        return calorieAiService.getCalorieAnalyze();
    }


}

