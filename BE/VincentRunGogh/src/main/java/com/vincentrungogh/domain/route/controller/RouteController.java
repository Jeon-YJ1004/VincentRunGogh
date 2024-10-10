package com.vincentrungogh.domain.route.controller;

import com.vincentrungogh.domain.route.service.facade.RouteFacade;
import com.vincentrungogh.domain.route.service.dto.request.ArtRouteRequestDto;
import com.vincentrungogh.domain.route.service.dto.request.SaveRouteRequestDto;
import com.vincentrungogh.domain.route.service.dto.response.ArtRouteResponseDto;
import com.vincentrungogh.domain.route.service.dto.response.FindRouteResponseDto;
import com.vincentrungogh.domain.route.service.dto.response.SaveRouteResponseDto;
import com.vincentrungogh.global.auth.service.dto.response.UserPrincipal;
import com.vincentrungogh.global.util.CommonSwaggerResponse;
import com.vincentrungogh.global.util.ResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/routes")
@RequiredArgsConstructor
@Slf4j
public class RouteController {

    private final RouteFacade routeFacade;

    //루트 조회
    @Operation(summary = "루트 조회", description = "사용자가 그린 아트를 루트화하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "루트 조회에 성공했습니다.",
                    content = @Content(schema = @Schema(implementation = FindRouteResponseDto.class))),
            @ApiResponse(responseCode = "204", description = "조회할 데이터가 없습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "500", description = "루트를 조회하는데 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @GetMapping
    public ResponseEntity<ResultDto> getRoute(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam String type,
            @RequestParam Double lat,
            @RequestParam Double lng) {
        log.info("루트 조회: "+type+" "+lat+" "+lng);

        FindRouteResponseDto responseDto = routeFacade.getRoute(userPrincipal, type, lat, lng);

        return ResponseEntity.status(HttpStatus.OK).body(ResultDto.res(HttpStatus.OK.value(), "루트 조회에 성공했습니다.", responseDto));
    }

    //아트 루트화
    @Operation(summary = "아트 루트화", description = "사용자가 그린 아트를 루트화하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "루트화에 성공했습니다.",
                    content = @Content(schema = @Schema(implementation = ArtRouteResponseDto.class))),
            @ApiResponse(responseCode = "204", description = "루트화할 데이터가 없습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "500", description = "아트를 루트화 하는데 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @PostMapping
    public ResponseEntity<ResultDto> convertArtRoute(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody ArtRouteRequestDto requestDto) {
        log.info("아트 루트화: " + requestDto.toString());
        ArtRouteResponseDto responseDto = routeFacade.convertArtRoute(userPrincipal, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(ResultDto.res(HttpStatus.OK.value(), "루트화에 성공했습니다.", responseDto));
    }

    //루트 최종 생성
    @Operation(summary = "루트 최종 생성", description = "아트이미지와 루트명을 통해 루트 최종 생성하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "루트를 성공적으로 저장했습니다.",
                    content = @Content(schema = @Schema(implementation = SaveRouteResponseDto.class))),
            @ApiResponse(responseCode = "204", description = "저장할 루트가 없습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "500", description = "루트 생성에 실패하였습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @PostMapping("/end")
    public ResponseEntity<ResultDto> saveRoute(@AuthenticationPrincipal UserPrincipal userPrincipal, @Valid @RequestBody SaveRouteRequestDto requestDto) {
        log.info("루트 최종 저장: "+ requestDto.toString());
        SaveRouteResponseDto responseDto = routeFacade.saveRoute(userPrincipal, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(ResultDto.res(HttpStatus.OK.value(), "루트화를 성공적으로 저장했습니다.", responseDto));
    }

}
