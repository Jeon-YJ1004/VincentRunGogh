package com.vincentrungogh.domain.drawing.service.dto.response;

import com.vincentrungogh.domain.route.service.dto.common.Position;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StartDrawingResponse {
    private String title;
    private int drawingId;
    private List<Position> routePositionList;

    @Builder
    private StartDrawingResponse(String title, int drawingId, List<Position> routePositionList) {
        this.title = title;
        this.drawingId = drawingId;
        this.routePositionList = routePositionList;
    }

    public static StartDrawingResponse createStartDrawingResponse(String title, int drawingId, List<Position> routePositionList) {
        return StartDrawingResponse.builder()
                .title(title)
                .drawingId(drawingId)
                .routePositionList(routePositionList)
                .build();
    }

    public static StartDrawingResponse createStartFreeRunningResponse(int drawingId) {
        return StartDrawingResponse.builder()
                .title(null)
                .drawingId(drawingId)
                .routePositionList(new ArrayList<>())
                .build();
    }
}
