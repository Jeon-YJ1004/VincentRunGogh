package com.vincentrungogh.domain.route.service.dto.request;

import com.vincentrungogh.domain.route.service.dto.common.Position;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DataSaveRouteRequestDto {
    private int routeId;
    private List<Position> positionList;

    @Builder
    private DataSaveRouteRequestDto(int routeId, List<Position> positionList) {
        this.routeId = routeId;
        this.positionList = positionList;
    }

    public static DataSaveRouteRequestDto createDataSaveRouteRequestDto(int routeId, List<Position> positionList) {
        return DataSaveRouteRequestDto.builder()
                .routeId(routeId)
                .positionList(positionList)
                .build();
    }
}
