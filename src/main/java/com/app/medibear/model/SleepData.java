package com.app.medibear.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_activities_tb")
public class SleepData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_no", nullable = false)
    private Long memberNo;

    private LocalDate date;

    private Double sleepHours;

    private Double caffeineMg;

    private Double alcoholConsumption;

    private Double physicalActivityHours;

    private Double predictedSleepQuality;

    private Double predictedFatigueScore;

    private String recommendedSleepRange;

    private String conditionLevel;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public SleepData() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberNo() {
        return memberNo;
    }
    public void setMemberNo(Long memberNo) {
        this.memberNo = memberNo;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getSleepHours() {
        return sleepHours;
    }
    public void setSleepHours(Double sleepHours) {
        this.sleepHours = sleepHours;
    }

    public Double getCaffeineMg() {
        return caffeineMg;
    }
    public void setCaffeineMg(Double caffeineMg) {
        this.caffeineMg = caffeineMg;
    }

    public Double getAlcoholConsumption() {
        return alcoholConsumption;
    }
    public void setAlcoholConsumption(Double alcoholConsumption) {
        this.alcoholConsumption = alcoholConsumption;
    }

    public Double getPhysicalActivityHours() {
        return physicalActivityHours;
    }
    public void setPhysicalActivityHours(Double physicalActivityHours) {
        this.physicalActivityHours = physicalActivityHours;
    }

    public Double getPredictedSleepQuality() {
        return predictedSleepQuality;
    }
    public void setPredictedSleepQuality(Double predictedSleepQuality) {
        this.predictedSleepQuality = predictedSleepQuality;
    }

    public Double getPredictedFatigueScore() {
        return predictedFatigueScore;
    }
    public void setPredictedFatigueScore(Double predictedFatigueScore) {
        this.predictedFatigueScore = predictedFatigueScore;
    }

    public String getRecommendedSleepRange() {
        return recommendedSleepRange;
    }
    public void setRecommendedSleepRange(String recommendedSleepRange) {
        this.recommendedSleepRange = recommendedSleepRange;
    }

    public String getConditionLevel() {
        return conditionLevel;
    }
    public void setConditionLevel(String conditionLevel) {
        this.conditionLevel = conditionLevel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
