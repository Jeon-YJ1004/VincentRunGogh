package com.vincentrungogh.domain.drawing.service.dto.response;

import com.vincentrungogh.domain.drawing.entity.Drawing;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DrawingResponseDto {
    private String title;
    private String drawingImage;
    private int avgPace;
    private Double distance;

    @Builder
    private DrawingResponseDto(String title, String drawingImage, int avgPace, Double distance) {
        this.title = title;
        this.drawingImage = drawingImage;
        this.avgPace = avgPace;
        this.distance = distance;
    }

    public static DrawingResponseDto createDrawingResponseDto(Drawing drawing, Double avgSpeed, int meterDistance) {

        Double distance = Math.round((meterDistance / 1000.0) * 100) / 100.0;
        int avgPace = 0;
        if(avgSpeed > 0) {
            avgPace = (int) Math.round((60.0 / (avgSpeed * 3.6)) * 60);
        }

        return DrawingResponseDto.builder()
                .title(drawing.getTitle())
                .drawingImage(drawing.getAccumulatedDrawingImage())
                .avgPace(avgPace)
                .distance(distance)
                .build();
    }
}
