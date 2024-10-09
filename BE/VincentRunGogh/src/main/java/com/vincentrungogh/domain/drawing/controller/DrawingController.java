package com.vincentrungogh.domain.drawing.controller;

import com.vincentrungogh.domain.drawing.service.dto.request.*;
import com.vincentrungogh.domain.drawing.service.dto.response.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.vincentrungogh.domain.drawing.service.DrawingService;
import com.vincentrungogh.domain.drawing.service.facade.DrawingFacade;
import com.vincentrungogh.domain.route.service.dto.response.FindRouteResponseDto;
import com.vincentrungogh.global.auth.service.dto.response.UserPrincipal;
import com.vincentrungogh.global.util.CommonSwaggerResponse;
import com.vincentrungogh.global.util.ResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/drawings")
@RequiredArgsConstructor
@Slf4j
public class DrawingController {

    private final DrawingFacade drawingFacade;
    private final DrawingService drawingService;

    //드로잉 조회
    @Operation(summary = "드로잉 조회", description = "미완성이거나 완료된 드로잉 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "드로잉 조회에 성공했습니다.",
                    content = @Content(schema = @Schema(implementation = FindRouteResponseDto.class))),
            @ApiResponse(responseCode = "204", description = "조회할 데이터가 없습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "500", description = "드로잉 조회하는데 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @GetMapping("")
    public ResponseEntity<ResultDto> getDrawingList(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam String type) {
        log.info("드로잉 조회: "+type);

        DrawingListResponseDto responseDto = drawingFacade.getDrawingList(userPrincipal.getId(), type);

        return ResponseEntity.status(HttpStatus.OK).body(ResultDto.res(HttpStatus.OK.value(), "루트 조회에 성공했습니다.", responseDto));
    }

    //특정 드로잉 조회
    @Operation(summary = "특정 드로잉 조회", description = "id를 통한 특정 드로잉 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "드로잉 조회에 성공했습니다.",
                    content = @Content(schema = @Schema(implementation = FindRouteResponseDto.class))),
            @ApiResponse(responseCode = "204", description = "조회할 데이터가 없습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "500", description = "드로잉 조회하는데 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @GetMapping("/{drawingId}")
    public ResponseEntity<ResultDto> getDrawing(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int drawingId) {
        log.info("특정 드로잉 조회: "+drawingId);

        DrawingResponseDto responseDto = drawingService.getDrawing(userPrincipal.getId(), drawingId);

        return ResponseEntity.status(HttpStatus.OK).body(ResultDto.res(HttpStatus.OK.value(), "루트 조회에 성공했습니다.", responseDto));
    }

    @Operation(summary = "드로잉 시작", description = "드로잉 생성 후 시작")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "드로잉이 시작되었습니다.",
                    content = @Content(schema = @Schema(implementation = StartDrawingResponse.class))),
            @ApiResponse(responseCode = "400", description = "3개의 드로잉이 진행 중입니다. 진행 중인 드로잉을 완료해주세요.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "404", description = "루트가 null입니다. 루트 드로잉을 시작할 수 없습니다",
                    content = @Content(schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "500", description = "드로잉을 시작하는데 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @PostMapping("/start")
    public ResponseEntity<?> startDrawing(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                          @RequestBody StartDrawingRequest request,
                                          @RequestParam String type){

        log.info("루트 아이디 "+ request.getRouteId());
        StartDrawingResponse response = drawingService.startDrawing(userPrincipal.getId(), request, type);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultDto.res(HttpStatus.OK.value(), "드로잉이 시작되었습니다.", response));
    }

    @Operation(summary = "드로잉 재시작", description = "드로잉 재시작")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "드로잉이 재시작되었습니다.",
                    content = @Content(schema = @Schema(implementation = RestartDrawingResponse.class))),
            @ApiResponse(responseCode = "500", description = "드로잉을 재시작하는데 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @PostMapping("/start/{drawingId}")
    public ResponseEntity<?> restartDrawing(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                            @PathVariable int drawingId,
                                            @RequestBody @Valid RestartDrawingRequest request){

        RestartDrawingResponse response = drawingService.restartDrawing(drawingId, request, userPrincipal.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultDto.res(HttpStatus.OK.value(), "드로잉이 시작되었습니다.", response));
    }

    @Operation(summary = "드로잉 저장", description = "드로잉 저장")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "드로잉이 저장되었습니다.",
                    content = @Content(schema = @Schema(implementation = SaveDrawingResponse.class))),
            @ApiResponse(responseCode = "500", description = "드로잉을 저장하는데 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @PostMapping("/{drawingId}")
    public ResponseEntity<?> saveDrawing(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                         @PathVariable int drawingId,
                                         @RequestBody @Valid SaveDrawingRequest request){

        SaveDrawingResponse response =  drawingService.saveDrawing(userPrincipal.getId(), drawingId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultDto.res(HttpStatus.OK.value(), "드로잉이 저장되었습니다.", response));

    }

    @Operation(summary = "드로잉 재저장", description = "드로잉 저장 시 오류가 발생했을 경우 " +
            "gps 배열을 받아서 저장 시도")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "드로잉이 저장되었습니다.",
                    content = @Content(schema = @Schema(implementation = SaveDrawingResponse.class))),
            @ApiResponse(responseCode = "500", description = "드로잉을 저장하는데 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @PostMapping("re/{drawingId}")
    public ResponseEntity<?> reSaveDrawing(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                         @PathVariable int drawingId,
                                         @RequestBody @Valid ReSaveDrawingRequest request){

        SaveDrawingResponse response =  drawingService.saveDrawing(userPrincipal.getId(), drawingId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultDto.res(HttpStatus.OK.value(), "드로잉이 저장되었습니다.", response));

    }

    @Operation(summary = "드로잉 완료", description = "드로잉 완료")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "드로잉이 완료되었습니다.",
                    content = @Content(schema = @Schema(implementation = SaveDrawingResponse.class))),
            @ApiResponse(responseCode = "500", description = "드로잉을 완료하는데 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @PostMapping("/end/{drawingId}")
    public ResponseEntity<?> completeDrawing(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                             @PathVariable int drawingId,
                                             @RequestBody @Valid CompleteDrawingRequest request){

        SaveDrawingResponse response =  drawingService.completeDrawing(userPrincipal.getId(), drawingId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultDto.res(HttpStatus.OK.value(), "드로잉이 완료되었습니다.", response));

    }

    @Operation(summary = "드로잉 재완료", description = "드로잉 완료 시 오류가 발생했을 경우 gps 정보를 받아 완료 요청 시도")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "드로잉이 완료되었습니다.",
                    content = @Content(schema = @Schema(implementation = SaveDrawingResponse.class))),
            @ApiResponse(responseCode = "500", description = "드로잉을 완료하는데 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @PostMapping("re/end/{drawingId}")
    public ResponseEntity<?> reCompleteDrawing(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                             @PathVariable int drawingId,
                                             @RequestBody @Valid ReCompleteDrawingRequest request){

        SaveDrawingResponse response =  drawingService.completeDrawing(userPrincipal.getId(), drawingId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultDto.res(HttpStatus.OK.value(), "드로잉이 완료되었습니다.", response));

    }

    @Operation(summary = "드로잉 디테일들 가져오기", description = "특정 드로잉에 대한 모든 드로잉 디테일 정보 가져오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 드로잉에 대한 드로잉 디테일들 조회에 성공하였습니다.",
                    content = @Content(schema = @Schema(implementation = DrawingDetailsResponse.class))),
            @ApiResponse(responseCode = "500", description = "특정 드로잉에 대한 드로잉 디테일들 조회에 실패하였습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @GetMapping("/{drawingId}/details")
    public ResponseEntity<?> getDrawingDetails(@PathVariable int drawingId,
                                               @AuthenticationPrincipal UserPrincipal userPrincipal){

        DrawingDetailsResponse response =  drawingService.getDrawingDetails(drawingId, userPrincipal.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultDto.res(HttpStatus.OK.value(), "특정 드로잉에 대한 드로잉 디테일들 조회에 성공하였습니다.", response));

    }


}
