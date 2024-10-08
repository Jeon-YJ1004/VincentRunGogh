package com.vincentrungogh.global.auth.controller;


import com.vincentrungogh.global.auth.service.AuthService;
import com.vincentrungogh.global.auth.service.dto.request.*;
import com.vincentrungogh.global.auth.service.dto.response.*;
import com.vincentrungogh.global.service.EmailService;
import com.vincentrungogh.global.util.CommonSwaggerResponse;
import com.vincentrungogh.global.util.ResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인", description = "사용자 로그인하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인에 성공하셨습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "401", description = "회원정보가 일치하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "500", description = "로그인에 실패하셨습니다.",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
    })
    @CommonSwaggerResponse.CommonResponses
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse httpServletResponse){

        log.info("AuthController : 로그인 시작");
        LoginResponse response = authService.login(loginRequest, httpServletResponse);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultDto.res(HttpStatus.OK.value(),
                        "로그인에 성공하셨습니다." , response));
    }

    @Operation(summary = "회원가입", description = "사용자 회원가입하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입에 성공하셨습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "500", description = "회원가입에 실패하셨습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequest signupRequest){
        authService.signup(signupRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultDto.res(HttpStatus.OK.value(),
                        "회원가입에 성공하셨습니다."));
    }

    @Operation(summary = "닉네임 중복 확인", description = "사용자 닉네임 중복 확인 ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 중복 요청에 성공하였습니다.",
                    content = @Content(schema = @Schema(implementation = FindDuplicatedResponse.class))),
            @ApiResponse(responseCode = "500", description = "닉네임 중복 요청에 실패하였습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @GetMapping("/find-nickname")
    public ResponseEntity<?> findNickname(@RequestParam("nickname") String nickname){

        FindDuplicatedResponse response = authService.findNickname(nickname);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultDto.res(HttpStatus.OK.value(),
                        "닉네임 중복 요청에 성공하였습니다.", response));
    }

    @Operation(summary = "이메일 중복 확인", description = "사용자 이메일 중복 확인 ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 중복 요청에 성공하였습니다.",
                    content = @Content(schema = @Schema(implementation = FindDuplicatedResponse.class))),
            @ApiResponse(responseCode = "500", description = "이메일 중복 요청에 실패하였습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @GetMapping("/find-email")
    public ResponseEntity<?> findEmail(@RequestParam("email") String email){

        FindDuplicatedResponse response = authService.findEmail(email);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultDto.res(HttpStatus.OK.value(),
                        "이메일 중복 요청에 성공하였습니다.", response));
    }
    @Operation(summary = "이메일 인증 코드 전송", description = "이메일 인증 코드 전송")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 코드 전송 요청이 성공하였습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "500", description = "인증 코드 전송 요청이 실패하였습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @PostMapping("/code/send")
    public ResponseEntity<?> sendCode(@RequestBody @Valid SendCodeRequest sendCodeRequest){

        authService.sendCode(sendCodeRequest.getEmail());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultDto.res(HttpStatus.OK.value(),
                        "인증 코드를 전송하였습니다."));
    }

    @Operation(summary = "로그아웃", description = "사용자 로그아웃하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃에 성공하셨습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "500", description = "로그아웃에 실패하셨습니다. 다시 시도해주세요",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal UserPrincipal userPrincipal){

        authService.logout(userPrincipal.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultDto.res(HttpStatus.OK.value(), "로그아웃에 성공하였습니다."));
    }

    @Operation(summary = "이메일 인증 코드 확인", description = "이메일 인증 코드 전송")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 코드 확인 요청이 성공하였습니다.",
                    content = @Content(schema = @Schema(implementation = CodeCheckResponse.class))),
            @ApiResponse(responseCode = "410", description = "인증 코드가 만료되었습니다",
                    content = @Content(schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "500", description = "인증 코드 확인 요청이 실패하였습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @PostMapping("/code/check")
    public ResponseEntity<?> codeCheck(@RequestBody @Valid CodeCheckRequest codeCheckRequest){

        CodeCheckResponse response = authService.codeCheck(codeCheckRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultDto.res(HttpStatus.OK.value(), "인증 코드 확인 요청이 성공하였습니다.", response));
    }

    @Operation(summary = "비밀번호 재발급", description = "비밀번호 재발급 후 이메일로 전송")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Refresh Token 재발급 요청에 성공하였습니다",
                    content = @Content(schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "400", description = "이메일과 생년월일이 일치하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "401", description = "만료된 리프레시 토큰입니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class))),
            @ApiResponse(responseCode = "500", description = "비밀번호 재발급에 실패하였습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequest request){

        authService.resetPassword(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultDto.res(HttpStatus.OK.value(), "비밀번호가 재발급되었습니다. 이메일을 확인해주세요."));
    }

    @Operation(summary = "accessToken 재발급", description = "accessToken 재발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Refresh Token 재발급 요청에 성공하였습니다.",
                    content = @Content(schema = @Schema(implementation = ReissueTokenResponse.class))),
            @ApiResponse(responseCode = "500", description = "accessToken 재발급에 실패했습니다.",
                    content = @Content(schema = @Schema(implementation = ResultDto.class)))
    })
    @CommonSwaggerResponse.CommonResponses
    @PostMapping("/token/reissue")
    public ResponseEntity<?> reissueAccessToken(HttpServletRequest request){
        log.info("리프레시토큰 재발급");
        ReissueTokenResponse response = authService.reissueAccessToken(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultDto.res(HttpStatus.OK.value(), "accessToken이 재발급되었습니다.", response));
    }
}
