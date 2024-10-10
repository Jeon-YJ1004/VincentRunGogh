package com.vincentrungogh.domain.drawing.service.dto.response;

import com.vincentrungogh.domain.route.service.dto.common.Position;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestartDrawingResponse {

    private String title;
    private List<List<Position>> drawingPositionList;
    private List<Position> routePositionList;

    @Builder
    private RestartDrawingResponse(String title, List<List<Position>> drawingPositionList, List<Position> routePositionList) {
        this.title = title;
        this.drawingPositionList = drawingPositionList;
        this.routePositionList = routePositionList;
    }

    public static RestartDrawingResponse createRestartDrawingResponse(String title,
                                                                      List<List<Position>> drawingPositionList, List<Position> routePositionList) {
        return RestartDrawingResponse.builder()
                .title(title)
                .drawingPositionList(drawingPositionList)
                .routePositionList(routePositionList)
                .build();
    }
}
