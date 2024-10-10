package com.vincentrungogh.domain.user.controller;

import com.vincentrungogh.domain.user.service.UserService;
import com.vincentrungogh.domain.user.service.dto.request.UpdatePasswordRequest;
import com.vincentrungogh.domain.user.service.dto.request.UpdateUserProfileRequest;
import com.vincentrungogh.domain.user.service.dto.response.UserProfileResponse;
import com.vincentrungogh.domain.user.service.dto.response.WeekExerciseResponse;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "프로필 조회", description = "사용자 프로필 조회하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 조회에 성공하였습니다.",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "500", description = "프로필 조회에 실패하였습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @GetMapping("")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal UserPrincipal userPrincipal){

        UserProfileResponse response = userService.getUserProfile(userPrincipal.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultDto.res(HttpStatus.OK.value(),
                        "프로필 조회에 성공하였습니다." , response));
    }

    @Operation(summary = "프로필 수정", description = "사용자 프로필 수정하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 수정에 성공하였습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "400", description = "몸무게와 키는 0 이상이어야 합니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "500", description = "프로필 수정에 실패하였습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @PutMapping("")
    public ResponseEntity<?> updateUserProfile(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody @Valid UpdateUserProfileRequest request){

        userService.updateUserProfile(userPrincipal.getId(), request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultDto.res(HttpStatus.OK.value(), "프로필 수정에 성공하였습니다."));
    }


    @Operation(summary = "프로필 이미지 수정", description = "사용자 이미지 프로필 수정하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 이미지 수정에 성공하였습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "500", description = "프로필 이미지 수정에 실패하였습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @PutMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfileImage(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam(name = "image", required = true) MultipartFile image){

        userService.updateProfileImage(userPrincipal.getId(), image);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultDto.res(HttpStatus.OK.value(), "프로필 이미지 수정에 성공하였습니다."));

    }

    @Operation(summary = "비밀번호 수정", description = "사용자 비밀번호 수정하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 수정에 성공하였습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "500", description = "비밀번호 수정에 실패하였습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@AuthenticationPrincipal UserPrincipal userPrincipal,@RequestBody @Valid UpdatePasswordRequest request
    ){

        userService.updatePassword(userPrincipal.getId(), request.getPassword());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultDto.res(HttpStatus.OK.value(), "비밀번호 수정에 성공하였습니다."));
    }

    @Operation(summary = "일주일 운동 정보 조회", description = "현재 날짜 포함 7일의 운동 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일주일 운동 정보 조회에 성공하였습니다.",
                    content = @Content(schema = @Schema(implementation = WeekExerciseResponse.class))),
            @ApiResponse(responseCode = "500", description = "일주일 운동 정보 조회에 실패하였습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @GetMapping("/week")
    public ResponseEntity<?> getWeekExercise(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        WeekExerciseResponse response = userService.getWeekExercise(userPrincipal.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultDto.res(HttpStatus.OK.value(), "일주일 운동 정보 조회에 성공하였습니다.", response));
    }

}
