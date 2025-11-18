package com.app.medibear.entity;

import com.app.medibear.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "fitness_log_tb")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FitnessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fitness_log_no")
    private Long fitnessLogNo;

    // 🔥 운동 정보
    @Column(name = "activity_type", nullable = false)
    private String activityType;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "calories_burned", nullable = false)
    private Double caloriesBurned;

    // 🔥 신체 정보 (이 기능에 필수)
    @Column(name = "weight_kg", nullable = false)
    private Double weightKg;

    @Column(name = "height_cm", nullable = false)
    private Double heightCm;

    @Column(name = "bmi", nullable = false)
    private Double bmi;

    // 🔥 시간 정보
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 🔥 멤버 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @Override
    public String toString() {
        return "FitnessLog{" +
            "updatedAt=" + updatedAt +
            ", createdAt=" + createdAt +
            ", bmi=" + bmi +
            ", heightCm=" + heightCm +
            ", weightKg=" + weightKg +
            ", caloriesBurned=" + caloriesBurned +
            ", durationMinutes=" + durationMinutes +
            ", activityType='" + activityType + '\'' +
            ", fitnessLogNo=" + fitnessLogNo +
            '}';
    }
}
