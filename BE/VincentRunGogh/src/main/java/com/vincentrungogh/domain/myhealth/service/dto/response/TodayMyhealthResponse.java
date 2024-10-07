package com.vincentrungogh.domain.myhealth.service.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodayMyhealthResponse {
    private int todayRuntime;
    private double todayDistance;
    private int todayAvgPace;
    private int todayStep;

    @Builder
    private TodayMyhealthResponse(int todayRuntime, double todayDistance, int todayAvgPace, int todayStep) {
        this.todayRuntime = todayRuntime;
        this.todayDistance = todayDistance;
        this.todayAvgPace = todayAvgPace;
        this.todayStep = todayStep;
    }

    public static TodayMyhealthResponse createTodayMyhealthResponse(int todayRuntime, double todayDistance, int todayAvgPace, int todayStep) {
        return TodayMyhealthResponse
                .builder()
                .todayRuntime(todayRuntime)
                .todayDistance(todayDistance)
                .todayAvgPace(todayAvgPace)
                .todayStep(todayStep)
                .build();
    }
}
