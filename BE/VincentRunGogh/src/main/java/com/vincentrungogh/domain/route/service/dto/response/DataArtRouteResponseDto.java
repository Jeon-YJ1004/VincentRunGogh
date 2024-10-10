package com.vincentrungogh.domain.route.service.dto.response;

import com.vincentrungogh.domain.route.service.dto.common.Position;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class DataArtRouteResponseDto {
    private List<Position> positionList;
}
