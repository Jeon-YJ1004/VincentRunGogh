package com.vincentrungogh.domain.route.service.dto.request;

import com.vincentrungogh.domain.route.service.dto.common.Position;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class ArtRouteRequestDto {
    private List<Position> positionList;

    private double leftLat; //좌상단 위도

    private double leftLng; //좌상단 경도

    private double rightLat; //우하단 위도

    private double rightLng; //우하단 경도
}
