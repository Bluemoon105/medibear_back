package com.app.medibear.controller;

import com.app.medibear.dto.UserInputRequest;
import com.app.medibear.model.SleepData;
import com.app.medibear.service.SleepService;
import com.app.medibear.utils.GetMemberId;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sleep")
public class SleepController {

    private final SleepService sleepService;
    private final GetMemberId getMemberId;

//    public SleepController(SleepService sleepService) {
//        this.sleepService = sleepService;
//    }

    /** 활동량 입력 — email 기반 */
    @PostMapping("/activities")
    public ResponseEntity<?> saveActivity(@RequestBody UserInputRequest input, HttpServletRequest request) {
    	 String authorizationHeader = request.getHeader("Authorization");
         String memberId =  getMemberId.getMemberId(authorizationHeader);
        try {
            SleepData saved = sleepService.saveInitialRecord(input);
            sleepService.updateFatiguePrediction(saved);
            sleepService.updateOptimalSleepRange(saved);

            return ResponseEntity.ok(new ApiResponse("데이터가 성공적으로 저장되었습니다.", saved));

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("서버 오류: " + e.getMessage(), null));
        }
    }

    /** 오늘 피로도 예측 — email 기반 */
    @GetMapping("/predict-fatigue")
    public ResponseEntity<ApiResponse> predictFatigue(@RequestParam("email") String email, HttpServletRequest request) {
    	 String authorizationHeader = request.getHeader("Authorization");
         String memberId =  getMemberId.getMemberId(authorizationHeader);
        SleepData todayRecord = sleepService.findTodayRecord(email, LocalDate.now());

        if (todayRecord == null) {
            return ResponseEntity.ok(new ApiResponse("오늘 입력된 데이터가 없습니다.", null));
        }

        SleepData updated = sleepService.updateFatiguePrediction(todayRecord);

        return ResponseEntity.ok(new ApiResponse("피로도 예측이 완료되었습니다.", updated));
    }

    /** 최적 수면시간 예측 — email 기반 */
    @GetMapping("/predict-sleephours")
    public ResponseEntity<ApiResponse> predictSleepHours(@RequestParam("email") String email, HttpServletRequest request) {
    	 String authorizationHeader = request.getHeader("Authorization");
         String memberId =  getMemberId.getMemberId(authorizationHeader);
        SleepData todayRecord = sleepService.findTodayRecord(email, LocalDate.now());

        if (todayRecord == null) {
            return ResponseEntity.ok(new ApiResponse("오늘 입력된 데이터가 없습니다.", null));
        }

        SleepData updated = sleepService.updateOptimalSleepRange(todayRecord);

        return ResponseEntity.ok(new ApiResponse("수면 시간 예측이 완료되었습니다.", updated));
    }

    /** 최근 7일 데이터 조회 — email 기반 */
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse> showRecent(@RequestParam("email") String email, HttpServletRequest request) {
    	 String authorizationHeader = request.getHeader("Authorization");
         String memberId =  getMemberId.getMemberId(authorizationHeader);
        List<SleepData> list = sleepService.getRecentSleepHours(email);

        return ResponseEntity.ok(new ApiResponse("최근 7일 데이터 조회 완료", list));
    }

    /** 공통 응답 DTO */
    record ApiResponse(String message, Object data) {}
}
