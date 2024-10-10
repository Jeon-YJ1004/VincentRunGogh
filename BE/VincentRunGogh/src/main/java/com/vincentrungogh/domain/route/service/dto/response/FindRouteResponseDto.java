package com.vincentrungogh.domain.route.service.dto.response;

import com.vincentrungogh.domain.route.service.dto.common.FindRoute;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FindRouteResponseDto {
    private List<FindRoute> routeList;


    @Builder
    private FindRouteResponseDto(List<FindRoute> routeList) {
        this.routeList = routeList;
    }

    public static FindRouteResponseDto createFindRouteResponseDto(List<FindRoute> routeList) {
        return FindRouteResponseDto.builder()
                .routeList(routeList)
                .build();
    }
}
