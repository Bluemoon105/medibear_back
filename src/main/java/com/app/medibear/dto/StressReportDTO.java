package com.app.medibear.dto;

import lombok.Data;

@Data
public class StressReportDTO {

    private Double sleepHours;
    private Double activityLevel;   // 0~10 등
    private Double caffeineCups;    // 하루 섭취 잔수
    private String primaryEmotion;  // 음성 분석 결과
    private String comment;

}
