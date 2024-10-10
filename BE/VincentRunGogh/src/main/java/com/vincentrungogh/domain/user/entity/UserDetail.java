package com.vincentrungogh.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users_detail")
public class UserDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    // 성별, 남자(0), 여자(1)
    @Column(name = "gender", nullable = false)
    private int gender;

    // 연령대
    @Column(name = "age", nullable = false)
    private int age;

    // 하루 평균 속도, km/h
    @Column(name = "average_day_speed")
    private double averageDaySpeed;

    // 하루 평균 거리, meter
    @Column(name = "average_day_distance")
    private int averageDayDistance;

    // 하루 평균 운동 시간, second
    @Column(name = "average_day_runtime")
    private int averageDayRuntime;

    @Builder
    private UserDetail(int gender, int age, double averageDaySpeed, int averageDayDistance, int averageDayRuntime) {
        this.gender = gender;
        this.age = age;
        this.averageDaySpeed = averageDaySpeed;
        this.averageDayDistance = averageDayDistance;
        this.averageDayRuntime = averageDayRuntime;
    }

    public static UserDetail createUserDetail(int gender, int age, double averageDaySpeed, int averageDayDistance, int averageDayRuntime) {
        return UserDetail.builder()
                .gender(gender)
                .age(age)
                .averageDaySpeed(averageDaySpeed)
                .averageDayDistance(averageDayDistance)
                .averageDayRuntime(averageDayRuntime)
                .build();
    }

    // 속도, 거리, 운동 시간 업데이트
    public void updateUserDetail(double averageDaySpeed, int averageDayDistance, int averageDayRuntime) {
        this.averageDaySpeed = averageDaySpeed;
        this.averageDayDistance = averageDayDistance;
        this.averageDayRuntime = averageDayRuntime;
    }
}
