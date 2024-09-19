package com.vincentrungogh.domain.route.service.Facade;

import com.vincentrungogh.domain.myhealth.entity.MyHealth;
import com.vincentrungogh.domain.myhealth.service.MyHealthService;
import com.vincentrungogh.domain.route.entity.Route;
import com.vincentrungogh.global.service.AwsService;
import com.vincentrungogh.global.service.PythonApiService;
import com.vincentrungogh.global.service.RedisService;
import com.vincentrungogh.domain.route.service.RouteService;
import com.vincentrungogh.domain.route.service.dto.common.Position;
import com.vincentrungogh.domain.route.service.dto.request.ArtRouteRequestDto;
import com.vincentrungogh.domain.route.service.dto.request.DataSaveRouteRequestDto;
import com.vincentrungogh.domain.route.service.dto.request.SaveRouteRequestDto;
import com.vincentrungogh.domain.route.service.dto.response.ArtRouteResponseDto;
import com.vincentrungogh.domain.route.service.dto.response.DataArtRouteResponseDto;
import com.vincentrungogh.domain.route.service.dto.response.DataSaveRouteResponseDto;
import com.vincentrungogh.domain.route.service.dto.response.SaveRouteResponseDto;
import com.vincentrungogh.domain.user.entity.User;
import com.vincentrungogh.domain.user.service.UserService;
import com.vincentrungogh.global.auth.service.dto.response.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

//복잡한 것을 피하기 위해 퍼사드 패턴 사용
@Service
@RequiredArgsConstructor
public class RouteFacade {
    private final UserService userService;
    private final RouteService routeService;
    private final RedisService redisService;
    private final PythonApiService pythonApiService;
    private final MyHealthService myHealthService;
    private final AwsService awsService;

    @Transactional
    public ArtRouteResponseDto convertArtRoute(UserPrincipal userPrincipal, ArtRouteRequestDto requestDto) {
        // 1. 사용자 확인
        User user = userService.getUserById(userPrincipal.getId());

        // 2. 파이썬 API 호출
        DataArtRouteResponseDto dataResponse = pythonApiService.convertArtRoute(requestDto);

        // 3. Redis에 임시 데이터 저장
        redisService.saveRoutePositionList(user.getId(), requestDto.getPositionList());

        // 4. 응답 생성
        return ArtRouteResponseDto.createArtRouteResponseDto(dataResponse.getPositionList());
    }

    @Transactional
    public SaveRouteResponseDto saveRoute(UserPrincipal userPrincipal, SaveRouteRequestDto requestDto) {
        // 1. 사용자 확인
        User user = userService.getUserById(userPrincipal.getId());

        // 2.aws 저장
        String imageUrl = awsService.uploadFile(requestDto.getArtImage());

        // 3. 루트 저장
        Route route = routeService.saveRoute(user, requestDto.getTitle(), imageUrl);

        // 4. Redis에서 좌표 가져오기
        List<Position> positionList = redisService.getRoutePositionList(user.getId());

        // 5. 파이썬 API 호출하여 데이터 저장
        DataSaveRouteRequestDto dataRequestDto = DataSaveRouteRequestDto.createDataSaveRouteRequestDto(route.getId(), positionList);
        DataSaveRouteResponseDto dataSaveResponse = pythonApiService.saveRoute(dataRequestDto);

        // 6. Route 업데이트
        routeService.updateRoute(route, dataSaveResponse.getCenterLat(), dataSaveResponse.getCenterLng(), dataSaveResponse.getDistance());

        // 7. MyHealth 정보 가져오기
        MyHealth myHealth = myHealthService.getMyHealth(user);

        return SaveRouteResponseDto.createSaveRouteResponseDto(route, myHealth.getAverageSpeed());
    }
}
