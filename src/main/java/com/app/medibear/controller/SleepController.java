package com.example.sleep.controller;

import com.example.sleep.dto.UserInputRequest;
import com.example.sleep.model.SleepData;
import com.example.sleep.service.SleepService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sleep")
public class SleepController {

    private final SleepService sleepService;

    public SleepController(SleepService sleepService) {
        this.sleepService = sleepService;
    }

    @PostMapping("/activities")
    public ResponseEntity<SleepData> saveActivity(@RequestBody UserInputRequest input) {
        SleepData saved = sleepService.saveInitialRecord(input);
        return ResponseEntity.ok(saved);
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
}