package com.vincentrungogh.domain.route.service.strategy;

import com.vincentrungogh.domain.board.entity.Board;
import com.vincentrungogh.domain.board.entity.UserLike;
import com.vincentrungogh.domain.board.repository.UserLikeRepository;
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
public class FindLikeRoute implements RouteStrategy {

    private final RouteRepository routeRepository;
    private final UserLikeRepository userLikeRepository;

    @Override
    public FindRouteResponseDto findRoute(User user, double lat, double lng, Double averageSpeed) {

        // 유저가 좋아요한 루트 정보를 모두 가져와 변환
        List<FindRoute> findRouteList = userLikeRepository.findAllByUser(user).stream()
                .map(UserLike::getBoard)               // UserLike 객체에서 Board 객체로 변환
                .filter(board -> !board.getIsDelete())
                .map(Board::getRoute)
                .filter(route -> route.getTitle() != null)// Board 객체에서 Route 객체로 변환
                .map(route -> FindRoute.createFindRoute(route, lat, lng, averageSpeed)) // FindRoute 객체 생성
                .filter(Objects::nonNull)              // null 값 제거
                .toList();

        return FindRouteResponseDto.createFindRouteResponseDto(findRouteList);
    }

}
