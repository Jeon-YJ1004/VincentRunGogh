package com.vincentrungogh.domain.route.service.dto.response;

import com.vincentrungogh.domain.route.entity.Route;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SaveRouteResponseDto {
    private String routeId;
    private String artImage;
    private Double distance;
    private int predictTime;

    @Builder
    private SaveRouteResponseDto(String routeId, String artImage, Double distance, int predictTime) {
        this.routeId = routeId;
        this.artImage = artImage;
        this.distance = distance;
        this.predictTime = predictTime;
    }

    public static SaveRouteResponseDto createSaveRouteResponseDto(Route route, double averageSpeed) {

        int meterDistance  = route.getDistance();
        Double distance = Math.round((meterDistance / 1000.0) * 100) / 100.0;

        int predictTime = 0;
        if(averageSpeed > 0) {
            predictTime = (int)(route.getDistance() / (averageSpeed * (5.0 / 18.0)));
        }

        return SaveRouteResponseDto.builder()
                .routeId(route.getId())
                .artImage(route.getArtImage())
                .distance(distance)
                .predictTime(predictTime)
                .build();
    }
}
