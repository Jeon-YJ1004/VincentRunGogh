package com.vincentrungogh.domain.calendar.service.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DrawingDetailForListOnSameDayResponse {
    private String drawingDetailId;
    private String drawingDetailImage;
    private Double drawingDetailDistance;
    private Integer drawingDetailTime;
    private Integer drawingDetailAvgPace;
    private LocalDateTime drawingDetailCreateTime;

    @Builder
    private DrawingDetailForListOnSameDayResponse(String drawingDetailId, String drawingDetailImage, Double drawingDetailDistance, Integer drawingDetailTime, Integer drawingDetailAvgPace, LocalDateTime drawingDetailCreateTime) {
        this.drawingDetailId = drawingDetailId;
        this.drawingDetailImage = drawingDetailImage;
        this.drawingDetailDistance = drawingDetailDistance;
        this.drawingDetailTime = drawingDetailTime;
        this.drawingDetailAvgPace = drawingDetailAvgPace;
        this.drawingDetailCreateTime = drawingDetailCreateTime;
    }

    public static DrawingDetailForListOnSameDayResponse createDrawingDetailForListOnSameDayResponse(String drawingDetailId, String drawingDetailImage, Double drawingDetailDistance, Integer drawingDetailTime, Integer drawingDetailAvgPace, LocalDateTime drawingDetailCreateTime) {
        return DrawingDetailForListOnSameDayResponse
                .builder()
                .drawingDetailId(drawingDetailId)
                .drawingDetailImage(drawingDetailImage)
                .drawingDetailDistance(drawingDetailDistance)
                .drawingDetailTime(drawingDetailTime)
                .drawingDetailAvgPace(drawingDetailAvgPace)
                .drawingDetailCreateTime(drawingDetailCreateTime)
                .build();
    }
}
