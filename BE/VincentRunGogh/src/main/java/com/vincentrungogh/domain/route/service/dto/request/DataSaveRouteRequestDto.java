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
    private List<Position> positionList;

    @Builder
    private DataSaveRouteRequestDto(List<Position> positionList) {
        this.positionList = positionList;
    }

    public static DataSaveRouteRequestDto createDataSaveRouteRequestDto(List<Position> positionList) {
        return DataSaveRouteRequestDto.builder()
                .positionList(positionList)
                .build();
    }
}
