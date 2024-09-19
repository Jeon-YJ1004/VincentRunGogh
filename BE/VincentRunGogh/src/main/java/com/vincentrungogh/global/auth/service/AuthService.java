package com.vincentrungogh.global.auth.service;

import com.vincentrungogh.domain.user.entity.User;
import com.vincentrungogh.domain.user.repository.UserRepository;
import com.vincentrungogh.global.auth.service.dto.request.LoginRequest;
import com.vincentrungogh.global.auth.service.dto.request.SignupRequest;
import com.vincentrungogh.global.auth.service.dto.response.FindDuplicatedResponse;
import com.vincentrungogh.global.auth.service.dto.response.LoginResponse;
import com.vincentrungogh.global.auth.service.dto.response.UserPrincipal;
import com.vincentrungogh.global.exception.CustomException;
import com.vincentrungogh.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authorizationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {
        log.info("AuthService : 로그인 시작");

        // 1. 유효성 확인
        Authentication authentication = authorizationManager.authenticate(
                // 아이디, 패스워드 입력
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // 2. 유저 정보 가져오기
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // 3. 토큰 생성
        String accessToken = jwtService.buildAccessToken(userPrincipal.getId());
        String refreshToken = jwtService.buildRefreshToken(userPrincipal.getId());

        // 4. refreshToken cookie에 넣기
        jwtService.addRefreshTokenToCookie(response, refreshToken);

        // 5. DB에 저장
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.addRefreshToken(refreshToken);
        userRepository.save(user);

        return LoginResponse.createLoginResponse(accessToken);
    }

    public void signup(SignupRequest signupRequest) {
        // 1. 이메일 중복 확인
        if(checkDuplicateEmail(signupRequest.getEmail())){
            throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
        }
        // 2. 닉네임 중복 확인
        if(checkDuplicateNickname(signupRequest.getNickname())){
            throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);
        }
        // 3. 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        // 4. User 엔티티 생성
        User user = User.createUser(
                signupRequest.getEmail(),
                encodedPassword,
                signupRequest.getNickname(),
                signupRequest.getGender(),
                signupRequest.getBirth(),
                signupRequest.getHeight(),
                signupRequest.getWeight());
        // 5. db 저장
        userRepository.save(user);
        return;
    }

    public FindDuplicatedResponse findNickname(String nickname){
        // 1. 닉네임 길이 확인
        if(nickname.length() > 10){
            throw new CustomException(ErrorCode.INVALID_NICKNAME_LENGTH);
        }
        // 2. 중복 확인
        boolean isDuplicated = checkDuplicateNickname(nickname);
        return FindDuplicatedResponse.createFindDuplicatedResponse(isDuplicated);
    }

    public FindDuplicatedResponse findEmail(String email){

        // 1. 중복 확인
        boolean isDuplicated = checkDuplicateEmail(email);
        return FindDuplicatedResponse.createFindDuplicatedResponse(isDuplicated);
    }

    private boolean checkDuplicateEmail(String email){
        Optional<?> user = userRepository.findByEmail(email);
        return user.isPresent();
    }

    private boolean checkDuplicateNickname(String nickname){
        Optional<?> user = userRepository.findByNickname(nickname);
        return user.isPresent();
    }
}
