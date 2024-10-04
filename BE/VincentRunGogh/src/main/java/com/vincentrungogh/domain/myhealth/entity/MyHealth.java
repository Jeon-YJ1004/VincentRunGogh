package com.vincentrungogh.domain.myhealth.entity;

import com.vincentrungogh.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "my_health")
public class MyHealth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    // 총 운동 시간
    @Column(name = "total_time")
    private int totalTime;

    // 총 운동 거리
    @Column(name = "total_distance")
    private int totalDistance;

    // 평균 속력
    @Column(name = "average_speed")
    private double averageSpeed;

    // 총 걸음 수
    @Column(name = "total_step")
    private int totalStep;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    private MyHealth(int totalTime, int totalDistance, double averageSpeed, int totalStep, User user) {
        this.totalTime = totalTime;
        this.totalDistance = totalDistance;
        this.averageSpeed = averageSpeed;
        this.totalStep = totalStep;
        this.user = user;
    }

    public static MyHealth createMyHealth(User user) {
        return MyHealth.builder()
                .totalTime(0)
                .totalDistance(0)
                .averageSpeed(0)
                .totalStep(0)
                .user(user)
                .build();
    }

    // 총 운동 시간/거리, 평균 속력, 총 걸을 수 업데이트
    public void updateMyHealth(int totalTime, int totalDistance, double averageSpeed, int totalStep){
        this.totalTime = totalTime;
        this.totalDistance = totalDistance;
        this.averageSpeed = averageSpeed;
        this.totalStep = totalStep;
    }
}
