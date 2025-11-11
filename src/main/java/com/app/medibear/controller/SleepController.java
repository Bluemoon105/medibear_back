package com.app.medibear.controller;

import com.app.medibear.dto.UserInputRequest;
import com.app.medibear.model.SleepData;
import com.app.medibear.service.SleepService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/sleep")
public class SleepController {

    private final SleepService sleepService;

    public SleepController(SleepService sleepService) {
        this.sleepService = sleepService;
    }

    /** 활동량 입력(하루 1회 제한) **/
    @PostMapping("/activities")
    public ResponseEntity<?> saveActivity(@RequestBody UserInputRequest input) {
        try {
            SleepData saved = sleepService.saveInitialRecord(input);
            return ResponseEntity.ok(saved);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류: " + e.getMessage());
        }
    }

    /** 피로도 예측 - 오늘 날짜 데이터 자동 조회 **/
    @PostMapping("/activities/predict-fatigue")
    public ResponseEntity<?> predictFatigueToday(@RequestParam("userId") String userId) { // ✅ String으로 변경
        SleepData todayRecord = sleepService.findTodayRecord(userId, LocalDate.now());
        if (todayRecord == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("오늘 입력된 데이터가 없습니다.");
        }

        SleepData updated = sleepService.updateFatiguePrediction(todayRecord);
        return ResponseEntity.ok(updated);
    }

    /** 수면 시간 예측 - 오늘 날짜 데이터 자동 조회 **/
    @PostMapping("/activities/predict-sleephours")
    public ResponseEntity<?> predictSleepHoursToday(@RequestParam("userId") String userId) { // ✅ String으로 변경
        SleepData todayRecord = sleepService.findTodayRecord(userId, LocalDate.now());
        if (todayRecord == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("오늘 입력된 데이터가 없습니다.");
        }

        SleepData updated = sleepService.updateOptimalSleepRange(todayRecord);
        return ResponseEntity.ok(updated);
    }

    /** 최근 수면 기록 조회 **/
    @GetMapping("/recent")
    public List<SleepData> getRecentSleepHours(@RequestParam("userId") String userId) { // ✅ String으로 변경
        return sleepService.getRecentSleepHours(userId);
    }
}
