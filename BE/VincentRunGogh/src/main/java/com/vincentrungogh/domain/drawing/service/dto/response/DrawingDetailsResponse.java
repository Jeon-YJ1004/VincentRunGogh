package com.vincentrungogh.domain.drawing.service.dto.response;

import com.vincentrungogh.domain.drawing.entity.Drawing;
import com.vincentrungogh.domain.drawing.entity.DrawingDetail;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DrawingDetailsResponse {

    private List<DrawingDetailResponseDto> drawingDetails;

    @Builder
    private DrawingDetailsResponse(List<DrawingDetailResponseDto> drawingDetails) {
        this.drawingDetails = drawingDetails;
    }

    public static DrawingDetailsResponse createDrawingDetailsResponse(List<DrawingDetail> drawingDetails) {

        List<DrawingDetailResponseDto> list = new ArrayList<>();
        for(DrawingDetail detail : drawingDetails) {
            list.add(DrawingDetailResponseDto.createDrawingDetailResponseDto(detail));
        }

        return DrawingDetailsResponse
                .builder()
                .drawingDetails(list)
                .build();
    }
}
