package com.vincentrungogh.domain.myhealth.service.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EachMonthMyhealthResponse {
    private int[] walkList;
    private double[] distanceList;
    private int[] timeList;
    private int[] completedRouteDrawingList;
    private int[] completedFreeDrawingList;
    private int totalWalk;
    private double totalDistance;
    private int totalTime;
    private int totalCompletedDrawing;

    @Builder
    private EachMonthMyhealthResponse(int[] walkList, double[] distanceList, int[] timeList, int[] completedRouteDrawingList, int[] completedFreeDrawingList, int totalWalk, double totalDistance, int totalTime, int totalCompletedDrawing) {
        this.walkList = walkList;
        this.distanceList = distanceList;
        this.timeList = timeList;
        this.completedRouteDrawingList = completedRouteDrawingList;
        this.completedFreeDrawingList = completedFreeDrawingList;
        this.totalWalk = totalWalk;
        this.totalDistance = totalDistance;
        this.totalTime = totalTime;
        this.totalCompletedDrawing = totalCompletedDrawing;
    }

    public static EachMonthMyhealthResponse createEachMonthMyhealthResponse(int[] walkList, double[] distanceList, int[] timeList, int[] completedRouteDrawingList, int[] completedFreeDrawingList, int totalWalk, double totalDistance, int totalTime, int totalCompletedDrawing) {
        return EachMonthMyhealthResponse
                .builder()
                .walkList(walkList)
                .distanceList(distanceList)
                .timeList(timeList)
                .completedRouteDrawingList(completedRouteDrawingList)
                .completedFreeDrawingList(completedFreeDrawingList)
                .totalWalk(totalWalk)
                .totalDistance(totalDistance)
                .totalTime(totalTime)
                .totalCompletedDrawing(totalCompletedDrawing)
                .build();
    }
}
