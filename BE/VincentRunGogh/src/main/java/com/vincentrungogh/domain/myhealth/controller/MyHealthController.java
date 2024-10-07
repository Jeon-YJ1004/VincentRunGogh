package com.vincentrungogh.domain.myhealth.controller;

import com.vincentrungogh.domain.myhealth.service.MyHealthService;
import com.vincentrungogh.domain.myhealth.service.dto.response.EachMonthMyhealthResponse;
import com.vincentrungogh.domain.myhealth.service.dto.response.TodayMyhealthResponse;
import com.vincentrungogh.global.auth.service.dto.response.UserPrincipal;
import com.vincentrungogh.global.util.CommonSwaggerResponse;
import com.vincentrungogh.global.util.ResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/myhealth")
@RequiredArgsConstructor
public class MyHealthController {

    private final MyHealthService myHealthService;

    // 드로잉 활동 정보 조회
    @Operation(summary = "드로잉 활동 정보 조회", description = "드로잉 활동 정보 조회하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "드로잉 활동 정보 조회 요청이 성공했습니다.",
                    content = @Content(schema = @Schema(implementation = EachMonthMyhealthResponse.class))),
            @ApiResponse(responseCode = "500", description = "드로잉 활동 정보 조회 요청이 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @GetMapping("/drawings")
    public ResponseEntity<?> getDrawings(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam int year) {
        EachMonthMyhealthResponse response = myHealthService.getDrawings(userPrincipal.getId(), year);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultDto.res(HttpStatus.OK.value(),
                        "드로잉 활동 정보 조회 요청이 성공했습니다.", response));
    }

    // 마이헬스 정보 조회
    @Operation(summary = "마이헬스 정보 조회", description = "마이헬스 정보 조회하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "마이헬스 정보 조회 요청이 성공했습니다.",
                    content = @Content(schema = @Schema(implementation = TodayMyhealthResponse.class))),
            @ApiResponse(responseCode = "500", description = "마이헬스 정보 조회 요청이 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @GetMapping("/group")
    public ResponseEntity<?> getToday(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        TodayMyhealthResponse response = myHealthService.getToday(userPrincipal.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultDto.res(HttpStatus.OK.value(),
                        "마이헬스 정보 조회 요청이 성공했습니다.", response));
    }
}
