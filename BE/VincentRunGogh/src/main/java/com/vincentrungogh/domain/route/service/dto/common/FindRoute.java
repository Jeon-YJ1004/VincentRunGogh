package com.vincentrungogh.domain.route.service.dto.common;

import com.vincentrungogh.domain.route.entity.Route;
import com.vincentrungogh.global.exception.CustomException;
import com.vincentrungogh.global.exception.ErrorCode;
import com.vincentrungogh.global.util.DistanceCalculator;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FindRoute {
    private String routeId;
    private String title;
    private String artImage;
    private Double distance;
    private int predictTime;
    private Double distanceFromUser;


    @Builder
    private FindRoute(String routeId, String title, String artImage, Double distance, int predictTime, Double distanceFromUser) {
        this.routeId = routeId;
        this.title = title;
        this.artImage = artImage;
        this.distance = distance;
        this.predictTime = predictTime;
        this.distanceFromUser = distanceFromUser;
    }

    public static FindRoute createFindRoute(Route route, Double lat, Double lng, Double averageSpeed) {

        //사용자로부터 떨어진 거리
        Double distanceUser = DistanceCalculator.calculateDistance(route.getCenterLat(), route.getCenterLng(), lat, lng);

        int predictTime = 0;
        if(averageSpeed > 0) {
            predictTime = (int)(route.getDistance() / (averageSpeed * (5.0 / 18.0)));
        }

        Double distance = Math.round((route.getDistance() / 1000.0) * 100) / 100.0;

        return FindRoute.builder()
                .routeId(route.getId())
                .title(route.getTitle())
                .artImage(route.getArtImage())
                .distance(distance)
                .predictTime(predictTime)
                .distanceFromUser(distanceUser)
                .build();
    }
}
