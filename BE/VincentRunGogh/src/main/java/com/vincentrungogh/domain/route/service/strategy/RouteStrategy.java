package com.vincentrungogh.domain.route.service.strategy;


import com.vincentrungogh.domain.route.service.dto.common.FindRoute;
import com.vincentrungogh.domain.route.service.dto.response.FindRouteResponseDto;
import com.vincentrungogh.domain.user.entity.User;

//동적으로 타입이 정해지기 때문에 전략 패턴 사용
public interface RouteStrategy {
    FindRouteResponseDto findRoute(User user, double lat, double lng, Double averageSpeed);
}
