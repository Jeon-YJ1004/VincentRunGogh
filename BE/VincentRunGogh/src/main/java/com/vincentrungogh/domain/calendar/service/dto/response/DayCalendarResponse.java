package com.vincentrungogh.domain.calendar.service.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DayCalendarResponse {
    private LocalDate date;
    private Boolean isRunning;
    private Boolean isDrawing;
    private Integer dayTotalTime;
    private Double dayTotalDistance;
    private List<DrawingLastDayResponse> drawingList;

    @Builder
    private DayCalendarResponse(LocalDate date, Boolean isRunning, Boolean isDrawing, Integer dayTotalTime, Double dayTotalDistance, List<DrawingLastDayResponse> drawingList) {
        this.date = date;
        this.isRunning = isRunning;
        this.isDrawing = isDrawing;
        this.dayTotalTime = dayTotalTime;
        this.dayTotalDistance = dayTotalDistance;
        this.drawingList = drawingList;
    }

    public static DayCalendarResponse createDayCalendarResponse(LocalDate date, Boolean isRunning, Boolean isDrawing, Integer dayTotalTime, Double dayTotalDistance, List<DrawingLastDayResponse> drawingList) {
        return DayCalendarResponse
                .builder()
                .date(date)
                .isRunning(isRunning)
                .isDrawing(isDrawing)
                .dayTotalTime(dayTotalTime)
                .dayTotalDistance(dayTotalDistance)
                .drawingList(drawingList)
                .build();
    }
}
