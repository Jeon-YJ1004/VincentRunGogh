package com.vincentrungogh.domain.route.service.strategy;

import com.vincentrungogh.domain.route.entity.Route;
import com.vincentrungogh.domain.route.repository.RouteRepository;
import com.vincentrungogh.domain.route.service.dto.common.FindRoute;
import com.vincentrungogh.domain.route.service.dto.response.FindRouteResponseDto;
import com.vincentrungogh.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FindOthersRoute implements RouteStrategy {

    private final RouteRepository routeRepository;

    @Override
    public FindRouteResponseDto findRoute(User user, double lat, double lng, Double averageSpeed) {

        //유저가 가지지 않는 루트 mysql에서 모두 조회
        List<Route> routeList = routeRepository.findAllByUserIsNotOrderByCreatedDesc(user);

        List<FindRoute> findRouteList = routeList.stream()
                .filter(route -> route.getTitle() != null)
                .map(route -> FindRoute.createFindRoute(route, lat, lng, averageSpeed)) // 람다식 사용
                .filter(Objects::nonNull) // null 값 제거
                .toList();

        return FindRouteResponseDto.createFindRouteResponseDto(findRouteList);
    }

}
