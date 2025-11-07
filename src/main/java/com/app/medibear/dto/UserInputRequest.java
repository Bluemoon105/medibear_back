package com.app.medibear.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInputRequest {
    private Long userId;                   // 프론트에서 전달하는 로그인 사용자 ID
    private Double sleepHours;             // 수면 시간
    private Double caffeineMg;             // 카페인 섭취량
    private Double alcoholConsumption;     // 알코올 섭취량
    private Double physicalActivityHours;  // 신체활동 시간
}