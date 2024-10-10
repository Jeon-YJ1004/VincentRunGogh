package com.vincentrungogh.domain.route.service.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DataSaveRouteResponseDto {
    private String routeId;
    private double centerLat;
    private double centerLng;
    private int distance;
}
