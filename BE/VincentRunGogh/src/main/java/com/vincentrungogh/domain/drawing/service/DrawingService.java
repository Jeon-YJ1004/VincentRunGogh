package com.vincentrungogh.domain.drawing.service;

import com.vincentrungogh.domain.drawing.entity.DrawingDetail;
import com.vincentrungogh.domain.drawing.entity.MongoDrawingDetail;
import com.vincentrungogh.domain.drawing.repository.MongoDrawingRepository;
import com.vincentrungogh.domain.drawing.service.dto.request.*;
import com.vincentrungogh.domain.drawing.service.dto.response.*;
import com.vincentrungogh.domain.route.entity.MongoRoute;
import com.vincentrungogh.domain.route.entity.Route;
import com.vincentrungogh.domain.route.repository.MongoRouteRepository;
import com.vincentrungogh.domain.route.repository.RouteRepository;
import com.vincentrungogh.domain.route.service.dto.common.Position;
import com.vincentrungogh.domain.running.service.dto.request.RunningRequest;
import com.vincentrungogh.domain.user.service.UserService;
import com.vincentrungogh.global.service.AwsService;
import com.vincentrungogh.global.service.PythonApiService;
import com.vincentrungogh.global.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.vincentrungogh.domain.drawing.entity.Drawing;
import com.vincentrungogh.domain.drawing.repository.DrawingDetailRepository;
import com.vincentrungogh.domain.drawing.repository.DrawingRepository;
import com.vincentrungogh.domain.user.entity.User;
import com.vincentrungogh.domain.user.repository.UserRepository;
import com.vincentrungogh.global.exception.CustomException;
import com.vincentrungogh.global.exception.ErrorCode;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class DrawingService {

    private final DrawingDetailRepository drawingDetailRepository;
    private final DrawingRepository drawingRepository;
    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final MongoDrawingRepository mongoDrawingRepository;
    private final MongoRouteRepository mongoRouteRepository;
    private final UserService userService;
    private final RedisService redisService;
    private final PythonApiService pythonApiService;
    private final AwsService awsService;

    @Transactional
    public DrawingResponseDto getDrawing(int userId, int drawingId) {
        //유저 존재하지 않으면 에러
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        //드로잉 존재하지 않으면 에러
        Drawing drawing = drawingRepository.findById(drawingId).orElseThrow(
                () -> new CustomException(ErrorCode.DRAWING_NOT_FOUND)
        );

        //드로잉 디테일에서 평균 속력이랑 총 거리 구하기
        double avgSpeed = drawingDetailRepository.findByDrawingAverageSpeed(drawing);
//        int distance = drawingDetailRepository.findByDrawingSumDistance(drawing);
        int distance = 0;
        if(drawing.getRoute()!=null) {
            distance = drawing.getRoute().getDistance();
        }

        return DrawingResponseDto.createDrawingResponseDto(drawing, avgSpeed, distance);
    }

    @Transactional
    public StartDrawingResponse startDrawing(int userId, StartDrawingRequest request) {
        // 0. 레디스 저장
        String routeId = request.getRouteId();
        redisService.removeRunning(userId);
        redisService.saveRunning(userId, RunningRequest.createRunningRequest(request.getLat(), request.getLng(), request.getTime()));

        // 1. 유저 여부
        User user = userService.getUserById(userId);

        // 2. route 여부
        if(routeId == null) {
            // 자유 달리기
            return freeRunning(user);
        }
        return drawingRunning(routeId, user);
    }

    private StartDrawingResponse freeRunning(User user) {
        // 1. 드로잉 생성
        Drawing drawing = Drawing
                .createFreeRunning(user);

        drawing = drawingRepository.save(drawing);
        return StartDrawingResponse
                .createStartFreeRunningResponse(drawing.getId());
    }


    private StartDrawingResponse drawingRunning(String routeId, User user) {

        // 0. 진행 중인 드로잉 개수
        int count = drawingRepository.countAllByUserAndIsCompleted(user, false);
        if(count >= 3){
            throw new CustomException(ErrorCode.MORE_THAN_THREE_DRAWINGS);
        }
        // 1. 루트 찾기
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROUTE_NOT_FOUND));

        // 2. 드로잉 생성
        Drawing drawing = Drawing
                .createDrawing(route.getTitle(),
                        user,
                        route);
        drawing = drawingRepository.save(drawing);

        // 3. 루트 정보 가져오기
        MongoRoute mongoRoute = mongoRouteRepository.findById(routeId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROUTE_NOT_FOUND));

        return StartDrawingResponse
                .createStartDrawingResponse(drawing.getTitle(),
                        drawing.getId(), mongoRoute.getPositionList());

    }

    public RestartDrawingResponse restartDrawing(int drawingId, RestartDrawingRequest request, int userId){
        // 0. 레디스 저장
        redisService.removeRunning(userId);
        redisService.saveRunning(userId, RunningRequest.createRunningRequest(request.getLat(), request.getLng(), request.getTime()));

        // 1. 드로잉 찾기
        Drawing drawing = drawingRepository.findById(drawingId)
                .orElseThrow(() -> new CustomException(ErrorCode.DRAWING_NOT_FOUND));

        // 2. 드로잉디테일 찾기
        List<String> drawingDetailIds = drawingDetailRepository.findAllIdsByDrawing(drawing);

        // 3. 드로잉 디테일 정보 찾기
        List<MongoDrawingDetail> mongoDrawingPositionList = mongoDrawingRepository.findAllByIdIn(drawingDetailIds);

        // 4.
        List<Position> drawingPositionList = mongoDrawingPositionList.stream()
                .flatMap(mongoDrawing -> mongoDrawing.getPositionList().stream())
                .toList();

        // 5. 루트 정보
        List<Position> routePositionList = mongoRouteRepository.findById(drawing.getRoute().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROUTE_NOT_FOUND)).getPositionList();

        return RestartDrawingResponse.createRestartDrawingResponse(
                drawing.getTitle(),
                drawingPositionList,
                routePositionList
        );
    }

    @Transactional
    public SaveDrawingResponse saveDrawing(int userId, int drawingId, SaveDrawingRequest request) {
        // 0. 레디스 저장
        redisService.saveRunning(userId, RunningRequest.createRunningRequest(request.getLat(), request.getLng(), request.getTime()));

        // 1. 파이썬 호출
        DataSaveDrawingDetailResponse response = processDrawing(userId);

        // 2. 드로잉
        Drawing drawing = drawingRepository.findById(drawingId)
                .orElseThrow(() -> new CustomException(ErrorCode.DRAWING_NOT_FOUND));

        // 3. 드로잉 업데이트
        String drawingImageURL = this.getImageUrl(request.getDrawingImage());
        drawing.changeAccumulatedDrawingImage(drawingImageURL);
        drawingRepository.save(drawing);

        // 4. 드로잉 디테일 저장
        String drawingDetailImageURL = this.getImageUrl(request.getDrawingDetailImage());
        DrawingDetail drawingDetail = DrawingDetail
                .createDrawingDetail(response, drawingDetailImageURL,
                        drawing);
        drawingDetailRepository.save(drawingDetail);

        return SaveDrawingResponse
                .createSaveDrawingResponse(drawingImageURL, drawingDetailImageURL);
    }

    @Transactional
    public SaveDrawingResponse completeDrawing(int userId, int drawingId, CompleteDrawingRequest request) {
        // 0. 레디스 저장
        redisService.saveRunning(userId, RunningRequest.createRunningRequest(request.getLat(), request.getLng(), request.getTime()));

        // 1. 파이썬 호출
        DataSaveDrawingDetailResponse response = processDrawing(userId);

        // 2. 드로잉
        Drawing drawing = drawingRepository.findById(drawingId)
                .orElseThrow(() -> new CustomException(ErrorCode.DRAWING_NOT_FOUND));

        // 3. 드로잉 업데이트
        String drawingImageURL = this.getImageUrl(request.getDrawingImage());
        drawing.completeDrawing(request.getTitle(), drawingImageURL);

        // 4. 드로잉 디테일 저장
        String drawingDetailImageURL = this.getImageUrl(request.getDrawingDetailImage());
        DrawingDetail drawingDetail = DrawingDetail
                .completeDrawingDetail(response, drawingDetailImageURL,
                        drawing);
        drawingDetailRepository.save(drawingDetail);

        return SaveDrawingResponse
                .createSaveDrawingResponse(drawingImageURL, drawingDetailImageURL);
    }

    private DataSaveDrawingDetailResponse processDrawing(int userId) {
        // 1. redis에서 정보 가져오기
        List<RunningRequest> redisPositionList = redisService.getRunning(userId);

        // 2. python 연결
        DataSaveDrawingDetailResponse response = pythonApiService.saveDrawingDetail(
                DataSaveDrawingDetailRequest.createDataSaveDrawingDetailRequset(redisPositionList)
        );

        log.info("saveDrawing : " + response);

        // 3. 레디스 삭제
        redisService.removeRunning(userId);

        return response;
    }

    private String getImageUrl(String image){
        String fileName = awsService.uploadDrawingFile(image);
        return awsService.getImageUrl(fileName);
    }
}
