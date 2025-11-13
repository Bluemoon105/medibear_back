package com.app.medibear.calorie.controller;

import com.app.medibear.calorie.service.face.CalorieService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/calorie")
public class CalorieController {
    private final CalorieService calorieService;

    @GetMapping("/test")
    public void test() {
        calorieService.addTest();
    }


}
