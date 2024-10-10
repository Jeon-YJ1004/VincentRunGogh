package com.vincentrungogh.domain.user.service.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeekExerciseResponse {

    private double[] distance = new double[7];
    private int[] time = new int[7];

    @Builder
    private WeekExerciseResponse(double[] distance, int[] time) {
        this.distance = distance;
        this.time = time;
    }

    public static WeekExerciseResponse createWeekExerciseResponse(double[] distance, int[] time) {
        return WeekExerciseResponse.builder()
                .distance(distance)
                .time(time)
                .build();
    }
}
