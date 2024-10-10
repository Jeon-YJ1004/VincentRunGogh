package com.vincentrungogh.domain.calendar.service.dto.response;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class DrawingLastDayResponse {
    private Integer drawingId;
    private String drawingName;
    private Integer drawingTime;
    private Double drawingDistance;
    private Boolean isCompleted;

    @Builder
    private DrawingLastDayResponse(Integer drawingId, String drawingName, Integer drawingTime, Double drawingDistance, Boolean isCompleted) {
        this.drawingId = drawingId;
        this.drawingName = drawingName;
        this.drawingTime = drawingTime;
        this.drawingDistance = drawingDistance;
        this.isCompleted = isCompleted;
    }

    public static DrawingLastDayResponse createDrawingLastDayResponse(Integer drawingId, String drawingName, Integer drawingTime, Double drawingDistance, Boolean isCompleted) {
        return DrawingLastDayResponse
                .builder()
                .drawingId(drawingId)
                .drawingName(drawingName)
                .drawingTime(drawingTime)
                .drawingDistance(drawingDistance)
                .isCompleted(isCompleted)
                .build();
    }
}
