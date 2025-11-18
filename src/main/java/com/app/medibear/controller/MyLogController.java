package com.app.medibear.controller;

import com.app.medibear.dto.calorie.MyLogCalorieResponse;
import com.app.medibear.service.MyLogService;
import com.app.medibear.utils.GetMemberId;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mylog")
public class MyLogController {
    private final MyLogService myLogService;
    private final GetMemberId getMemberId;

    @GetMapping("/calorie")
    public MyLogCalorieResponse getCalorie(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String memberId =  getMemberId.getMemberId(authorizationHeader);

        return myLogService.getCalorieReport(memberId);

    }

}
