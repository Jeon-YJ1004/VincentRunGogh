package com.vincentrungogh.domain.myhealth.controller;

import com.vincentrungogh.domain.myhealth.service.MyHealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/myhealth")
@RequiredArgsConstructor
public class MyHealthController {

    private final MyHealthService myHealthService;

//    // 드로잉 활동 정보 조회
//    @Operation(summary = "드로잉 활동 정보 조회", description = "드로잉 활동 정보 조회하기")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "드로잉 활동 정보 조회 요청이 성공했습니다.",
//                    content = @Content(schema = @Schema(implementation = )))
//    })

}
