package com.app.medibear.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInputRequest {

    private String email;

    private Double sleepHours;

    private Double caffeineMg;

    private Double alcoholConsumption;

    private Double physicalActivityHours;
}
