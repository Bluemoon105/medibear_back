package com.app.medibear.controller;

import com.app.medibear.dto.UserInputRequest;
import com.app.medibear.model.SleepData;
import com.app.medibear.service.SleepService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sleep")
public class SleepController {

    private final SleepService sleepService;

    public SleepController(SleepService sleepService) {
        this.sleepService = sleepService;
    }

    @PostMapping("/activities")
    public ResponseEntity<?> saveActivity(@RequestBody UserInputRequest input) {
        try {
            SleepData saved = sleepService.saveInitialRecord(input);
            return ResponseEntity.ok(saved);
        } catch (IllegalStateException e) {
            // 하루에 이미 입력된 경우
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // 그 외 예외
            return ResponseEntity.internalServerError().body("서버 오류: " + e.getMessage());
        }
    }

    @PostMapping("/activities/{id}/predict-fatigue")
    public ResponseEntity<SleepData> predictFatigue(@PathVariable("id") Long id) {
        SleepData record = sleepService.updateFatiguePrediction(sleepService.findById(id));
        return ResponseEntity.ok(record);
    }

    @PostMapping("/activities/{id}/predict-sleephours")
    public ResponseEntity<SleepData> predictOptimal(@PathVariable("id") Long id) {
        SleepData record = sleepService.updateOptimalSleepRange(sleepService.findById(id));
        return ResponseEntity.ok(record);
    }
    
    @GetMapping("/recent")
    public List<SleepData> getRecentSleepHours(@RequestParam ("userId") Long userId) {
    	return sleepService.getRecentSleepHours(userId);
    }
}