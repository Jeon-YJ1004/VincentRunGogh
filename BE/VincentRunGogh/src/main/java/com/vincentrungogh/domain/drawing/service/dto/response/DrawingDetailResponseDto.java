package com.vincentrungogh.domain.drawing.service.dto.response;

import com.vincentrungogh.domain.drawing.entity.DrawingDetail;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DrawingDetailResponseDto {
    private int time;
    private int distance;
    private double speed;
    private int step;
    private String drawingDetailImage;
    private LocalDateTime created;

    @Builder
    private DrawingDetailResponseDto(int time, int distance, double speed, int step, String drawingDetailImage, LocalDateTime created) {
        this.time = time;
        this.distance = distance;
        this.speed = speed;
        this.step = step;
        this.drawingDetailImage = drawingDetailImage;
        this.created = created;
    }

    public static DrawingDetailResponseDto createDrawingDetailResponseDto(DrawingDetail detail){

        return DrawingDetailResponseDto
                .builder()
                .time(detail.getTime())
                .distance(detail.getDistance())
                .speed(detail.getSpeed())
                .step(detail.getStep())
                .drawingDetailImage(detail.getCurrentDrawingImage())
                .created(detail.getCreated())
                .build();
    }
}
