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
    private String drawingDetailImage;
    private LocalDateTime created;

    @Builder
    private DrawingDetailResponseDto(String drawingDetailImage, LocalDateTime created) {
        this.drawingDetailImage = drawingDetailImage;
        this.created = created;
    }

    public static DrawingDetailResponseDto createDrawingDetailResponseDto(DrawingDetail detail){

        return DrawingDetailResponseDto
                .builder()
                .drawingDetailImage(detail.getCurrentDrawingImage())
                .created(detail.getCreated())
                .build();
    }
}
