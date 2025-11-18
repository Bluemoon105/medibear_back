package com.app.medibear.controller;

import com.app.medibear.dto.calorie.CalorieAnalysisResponse;
import com.app.medibear.dto.calorie.CaloriePredictRequest;
import com.app.medibear.dto.calorie.CaloriePredictResponse;

import com.app.medibear.service.CalorieService;
import com.app.medibear.utils.GetMemberId;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/calorie")
@Slf4j
public class CalorieController {

    private final CalorieService calorieService;
    private final GetMemberId getMemberId;
    // 클라이언트 -> 서버 : Request
    // 서버 -> 클라이언트 : Response

    @RequestMapping(value="/predict", method=RequestMethod.POST)
    public CaloriePredictResponse caloriePrediction(@RequestBody CaloriePredictRequest calorieRequest, HttpServletRequest request) {
        log.info("Authorization header: {}", request.getHeader("Authorization"));
        String authorizationHeader = request.getHeader("Authorization");
        String memberId =  getMemberId.getMemberId(authorizationHeader);
        log.info("memberId: {}", memberId);
        return calorieService.getCaloriePrediction(calorieRequest, memberId);
    }


    @RequestMapping(value="/analyze", method = RequestMethod.POST)
    public CalorieAnalysisResponse calorieLLMAnalyze(HttpServletRequest request) {
        // 받아야 할 데이터 : memberId
        String authorizationHeader = request.getHeader("Authorization");
        // 이메일값
        String memberId =  getMemberId.getMemberId(authorizationHeader);
        CalorieAnalysisResponse report = calorieService.getCalorieAnalyze(memberId);
        log.info("report: {}", report);
        return report;
    }

}
