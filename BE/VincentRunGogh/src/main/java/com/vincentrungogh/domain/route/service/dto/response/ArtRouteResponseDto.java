package com.vincentrungogh.domain.route.service.dto.response;

import com.vincentrungogh.domain.route.service.dto.common.Position;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtRouteResponseDto {
    private List<Position> positionList; //루트화된 {lat, lng} 리스트

    @Builder
    private ArtRouteResponseDto(List<Position> positionList) {
        this.positionList = positionList;
    }

    public static ArtRouteResponseDto createArtRouteResponseDto(List<Position> positionList) {
        return ArtRouteResponseDto.builder()
                .positionList(positionList)
                .build();
    }
}
