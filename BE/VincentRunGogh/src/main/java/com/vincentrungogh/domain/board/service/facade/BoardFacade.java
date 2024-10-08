package com.vincentrungogh.domain.board.service.facade;

import com.vincentrungogh.domain.board.entity.BoardType;
import com.vincentrungogh.domain.board.service.BoardContext;
import com.vincentrungogh.domain.board.service.BoardService;
import com.vincentrungogh.domain.board.service.dto.request.SaveBoardRequestDto;
import com.vincentrungogh.domain.board.service.dto.response.FindBoardResponseDto;
import com.vincentrungogh.domain.drawing.entity.Drawing;
import com.vincentrungogh.domain.drawing.entity.MongoDrawingDetail;
import com.vincentrungogh.domain.drawing.repository.DrawingDetailRepository;
import com.vincentrungogh.domain.drawing.repository.DrawingRepository;
import com.vincentrungogh.domain.drawing.repository.MongoDrawingRepository;
import com.vincentrungogh.domain.drawing.service.DrawingService;
import com.vincentrungogh.domain.route.entity.Route;
import com.vincentrungogh.domain.route.service.RouteService;
import com.vincentrungogh.domain.route.service.dto.common.Position;
import com.vincentrungogh.domain.route.service.dto.request.DataSaveRouteRequestDto;
import com.vincentrungogh.domain.route.service.dto.response.DataSaveRouteResponseDto;
import com.vincentrungogh.domain.user.entity.User;
import com.vincentrungogh.domain.user.service.UserService;
import com.vincentrungogh.global.auth.service.dto.response.UserPrincipal;
import com.vincentrungogh.global.exception.CustomException;
import com.vincentrungogh.global.exception.ErrorCode;
import com.vincentrungogh.global.service.AwsService;
import com.vincentrungogh.global.service.PythonApiService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardFacade {

    private final UserService userService;
    private final BoardContext boardContext;
    private final MongoDrawingRepository mongoDrawingRepository;
    private final DrawingRepository drawingRepository;
    private final DrawingDetailRepository drawingDetailRepository;
    private final PythonApiService pythonApiService;
    private final RouteService routeService;
    private final BoardService boardService;
    private final DrawingService drawingService;


    // getBoard
    @Transactional
    public FindBoardResponseDto getBoard(UserPrincipal userPrincipal, String type, Double lat, Double lng) {
        // 1. 먼저 사용자 확인하기
        User user = userService.getUserById(userPrincipal.getId());

        log.info("user: " + user.getEmail());
        // route와 같이 boardType 받아서 대조 후 에러 띄우기
        BoardType boardType;
        try{
            boardType = BoardType.valueOf(type.toUpperCase());
        } catch(IllegalArgumentException e){
            throw new CustomException(ErrorCode.INVALID_PARAM_TYPE);
        }

        FindBoardResponseDto responseDto = boardContext.findBoard(user, boardType, lat, lng);

        return responseDto;
    }

    // createBoard
    @Transactional
    public void createBoard(UserPrincipal userPrincipal, SaveBoardRequestDto requestDto) {

        // comment 30자 우선 검증
        if(requestDto.getComment().length() > 30) {
            throw new CustomException(ErrorCode.INVALID_COMMENT_LENGTH);
        }

        // 0. 사용자 정보
        User user = userService.getUserById(userPrincipal.getId());
        log.info("보드 생성 user: " + user.getEmail());

        // 1. 맨처음 드로잉 아이디로 드로잉 디테일에 있는 모든 디테일의 좌표(in mongoDB)를 긁어온다.
        // 1-1. 아이디로 드로잉 찾기
        Drawing drawing = drawingRepository.findById(requestDto.getDrawingId())
                .orElseThrow(() -> new CustomException(ErrorCode.DRAWING_NOT_FOUND));
        log.info("drawing 아이디는 잘 받아와지나? " + drawing.getId());
        // 1-2. 드로잉 디테일 찾기
        List<String> drawingDetailIds = drawingDetailRepository.findAllIdsByDrawing(drawing);
        log.info("drawing 디테일 아이디는 잘 받아와지나? " + drawingDetailIds);
        // 1-3. 드로잉 디테일에서 좌표 정보 찾기
        List<MongoDrawingDetail> mongoDrawingPositionList = mongoDrawingRepository.findAllByIdIn(drawingDetailIds);
        log.info("drawing mongoDB " + mongoDrawingPositionList);
        // 1-4. 좌표 정보 싹다 list화 시키기
        List<Position> drawingPositionList = mongoDrawingPositionList.stream()
                .flatMap(mongoDrawing -> mongoDrawing.getPositionList().stream())
                .toList();
        // 2. 긁어온 좌표들을 싹 다 가져와서 파이썬 서버에 보낸다(자유드로잉, 루트드로잉?)
        // 2-1. 가져온 좌표 기반 루트 생성하도록 파이썬에 보내기
        DataSaveRouteRequestDto dataRequestDto = DataSaveRouteRequestDto.createDataSaveRouteRequestDto(drawingPositionList);

        // 파이썬 서버에서 request는 좌표들, 반환된 것은(도로 좌표들)
        DataSaveRouteResponseDto dataResponseDto = pythonApiService.saveRoute(dataRequestDto);

        // 도로좌표를 받아오면 그걸 sql 루트에 저장을 해야함.
        // 루트, 자유
        // 루트, 자유 분기처리
        // 드로잉아이디로 접근했을 때 routeId가 null이면 자유드로잉으로 title 없음.
        // routeId있으면 해당 루트의 title 가져와야 함.

        // route의 imageUrl 가져오기
        // getRoute() 결과가 없다면 null이 되는 것
        log.info("드로잉의 route 정보 " + drawing.getRoute());
        String originRouteImageUrl = (drawing.getRoute() != null) ? drawing.getRoute().getArtImage() : null;
        String drawingImageUrl = drawing.getAccumulatedDrawingImage();
        Route route = routeService.saveRoute(user, drawing.getTitle(), originRouteImageUrl, drawingImageUrl, dataResponseDto);

        // board에 게시글 생성한 내용 넣기
        boardService.saveBoard(requestDto.getComment(),route);

        // 게시글 생성되면 drawingId로 가져온 drawing을 update
        drawing.createBoard();

    }

}
