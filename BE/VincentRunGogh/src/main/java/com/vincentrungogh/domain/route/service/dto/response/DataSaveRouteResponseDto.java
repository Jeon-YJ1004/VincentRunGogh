package com.vincentrungogh.domain.route.service.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DataSaveRouteResponseDto {
    private double centerLat;
    private double centerLng;
    private int distance;
}
