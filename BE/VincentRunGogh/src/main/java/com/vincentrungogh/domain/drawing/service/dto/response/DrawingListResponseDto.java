package com.vincentrungogh.domain.drawing.service.dto.response;

import com.vincentrungogh.domain.drawing.service.dto.common.FindDrawing;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DrawingListResponseDto {
    private List<FindDrawing> findDrawingList;

    @Builder
    private DrawingListResponseDto(List<FindDrawing> findDrawingList) {
        this.findDrawingList = findDrawingList;
    }

    public static DrawingListResponseDto createDrawingList(List<FindDrawing> findDrawing) {
        return DrawingListResponseDto.builder()
                .findDrawingList(findDrawing)
                .build();
    }
}
