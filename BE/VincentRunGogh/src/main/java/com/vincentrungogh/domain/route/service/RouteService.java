package com.vincentrungogh.domain.route.service;

import com.vincentrungogh.domain.myhealth.entity.MyHealth;
import com.vincentrungogh.domain.myhealth.repository.MyHealthRepository;
import com.vincentrungogh.domain.route.entity.Route;
import com.vincentrungogh.domain.route.repository.RouteRepository;
import com.vincentrungogh.domain.route.service.dto.response.DataSaveRouteResponseDto;
import com.vincentrungogh.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteService {
    private final RouteRepository routeRepository;

    //루트를 mysql에 저장
    public Route saveRoute(User user, String title, String artImage, DataSaveRouteResponseDto responseDto) {
        Route route = Route.createRoute(user, title, artImage, responseDto);
        routeRepository.save(route);
        return route;
    }

    public Route saveRoute(User user, String title, String artImage, String drawingImage, DataSaveRouteResponseDto responseDto) {
        Route route = Route.createaDrawingToRoute(user, title, artImage, drawingImage, responseDto);
        routeRepository.save(route);
        return route;
    }

    //루트를 업데이트
//    public void updateRoute(Route route, double centerLat, double centerLng, int distance) {
//        route.updateCenter(centerLat, centerLng, distance);
//        routeRepository.save(route);
//    }
}
