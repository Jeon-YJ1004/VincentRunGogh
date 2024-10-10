package com.vincentrungogh.domain.calendar.service.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DrawingDetailListOnSameDayResponse {
    private String title;
    private String routeImage;
    private List<DrawingDetailForListOnSameDayResponse> drawingDetailList;

    @Builder
    private DrawingDetailListOnSameDayResponse(String title, String routeImage, List<DrawingDetailForListOnSameDayResponse> drawingDetailList) {
        this.title = title;
        this.routeImage = routeImage;
        this.drawingDetailList = drawingDetailList;
    }

    public static DrawingDetailListOnSameDayResponse createDrawingDetailListOnSameDayResponse(String title, String routeImage, List<DrawingDetailForListOnSameDayResponse> drawingDetailList) {
        return DrawingDetailListOnSameDayResponse
                .builder()
                .title(title)
                .routeImage(routeImage)
                .drawingDetailList(drawingDetailList)
                .build();
    }
}
