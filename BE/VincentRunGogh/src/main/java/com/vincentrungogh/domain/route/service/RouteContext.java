package com.vincentrungogh.domain.route.service;

import com.vincentrungogh.domain.route.entity.RouteType;
import com.vincentrungogh.domain.route.service.dto.common.FindRoute;
import com.vincentrungogh.domain.route.service.dto.response.FindRouteResponseDto;
import com.vincentrungogh.domain.route.service.strategy.RouteStrategy;
import com.vincentrungogh.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RouteContext {
    //RouteStrategy 인터페이스를 구현한 컴포넌트들을 자동으로 autowired
    //autowired시 key는 클래스 이름을 카멜 케이스로 함
    private final Map<String, RouteStrategy> getRouteStrategyMap;

    public FindRouteResponseDto findRoute(User user, RouteType routeType, Double lat, Double lng, Double averageSpeed) {
        //타입 조회
        RouteStrategy routeStrategy = getRouteStrategyMap.get(routeType.getTypeName());
        
        //반환
        return routeStrategy.findRoute(user, lat, lng, averageSpeed);

    }
}
