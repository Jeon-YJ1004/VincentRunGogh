package com.vincentrungogh.domain.route.service.facade;

import com.vincentrungogh.domain.myhealth.entity.MyHealth;
import com.vincentrungogh.domain.myhealth.service.MyHealthService;
import com.vincentrungogh.domain.route.entity.Route;
import com.vincentrungogh.domain.route.entity.RouteType;
import com.vincentrungogh.domain.route.service.RouteContext;
import com.vincentrungogh.domain.route.service.dto.response.*;
import com.vincentrungogh.global.exception.CustomException;
import com.vincentrungogh.global.exception.ErrorCode;
import com.vincentrungogh.global.service.AwsService;
import com.vincentrungogh.global.service.PythonApiService;
import com.vincentrungogh.global.service.RedisService;
import com.vincentrungogh.domain.route.service.RouteService;
import com.vincentrungogh.domain.route.service.dto.common.Position;
import com.vincentrungogh.domain.route.service.dto.request.ArtRouteRequestDto;
import com.vincentrungogh.domain.route.service.dto.request.DataSaveRouteRequestDto;
import com.vincentrungogh.domain.route.service.dto.request.SaveRouteRequestDto;
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
    private final RouteContext routeContext;

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
        String saveUrl = awsService.uploadFile(requestDto.getArtImage());
        String imageUrl = awsService.getImageUrl(saveUrl);

        // 3. Redis에서 좌표 가져오기
        List<Position> positionList = redisService.getRoutePositionList(user.getId());

        // 4. 파이썬 API 호출하여 데이터 저장
        DataSaveRouteRequestDto dataRequestDto = DataSaveRouteRequestDto.createDataSaveRouteRequestDto(positionList);
        DataSaveRouteResponseDto dataSaveResponse = pythonApiService.saveRoute(dataRequestDto);

        // 5. 루트 저장
        Route route = routeService.saveRoute(user, requestDto.getTitle(), imageUrl, dataSaveResponse);

        // 6. Route에 존재하는 좌표들 삭제
        redisService.removeRoutePositionList(user.getId());

        // 7. MyHealth 정보 가져오기
        MyHealth myHealth = myHealthService.getMyHealth(user);

        return SaveRouteResponseDto.createSaveRouteResponseDto(route, myHealth.getAverageSpeed());
    }

    @Transactional
    public FindRouteResponseDto getRoute(UserPrincipal userPrincipal, String type, Double lat, Double lng) {
        // 1. 사용자 확인
        User user = userService.getUserById(userPrincipal.getId());

        // 2. 사용자 평균 속력
        MyHealth myHealth = myHealthService.getMyHealth(user);

        //파라미터로 들어온 타입이 mine, others, like 가 아니면 에러 발생
        RouteType routeType;
        try {
            routeType = RouteType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_PARAM_TYPE);
        }

        //전략 패턴을 이용하여 응답 반환
        FindRouteResponseDto responseDto = routeContext.findRoute(user, routeType, lat, lng, myHealth.getAverageSpeed());

        return responseDto;
    }

}
