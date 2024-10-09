package com.vincentrungogh.global.auth.service;

import com.vincentrungogh.domain.myhealth.entity.MyHealth;
import com.vincentrungogh.domain.myhealth.repository.MyHealthRepository;
import com.vincentrungogh.domain.user.entity.User;
import com.vincentrungogh.domain.user.repository.UserRepository;
import com.vincentrungogh.global.auth.service.dto.request.CodeCheckRequest;
import com.vincentrungogh.global.auth.service.dto.request.LoginRequest;
import com.vincentrungogh.global.auth.service.dto.request.ResetPasswordRequest;
import com.vincentrungogh.global.auth.service.dto.request.SignupRequest;
import com.vincentrungogh.global.auth.service.dto.response.*;
import com.vincentrungogh.global.exception.CustomException;
import com.vincentrungogh.global.exception.EmailServerException;
import com.vincentrungogh.global.exception.ErrorCode;
import com.vincentrungogh.global.service.EmailService;
import com.vincentrungogh.global.service.RedisService;
import com.vincentrungogh.global.util.PasswordGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authorizationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final MyHealthRepository myHealthRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final EmailService emailService;

    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {
        log.info("AuthService : 로그인 시작");
        // 0. 유저 존재 여부 확인
       userRepository.findByEmail(loginRequest.getEmail())
               .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

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

        // 5. redis 저장
        redisService.saveRefreshToken(userPrincipal.getId(), refreshToken);

        // 6. user 정보 가져오기
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return LoginResponse.createLoginResponse(accessToken, user.getIsChanged(), user.getNickname(), user.getProfile());
    }

    @Transactional
    public void signup(SignupRequest signupRequest) {
        // 1. 이메일 중복 확인
        if(checkDuplicateEmail(signupRequest.getEmail())){
            throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
        }
        // 2. 닉네임 중복 확인
        if(checkDuplicateNickname(signupRequest.getNickname())){
            throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);
        }
        // 3. 비밀번호 확인
        if(signupRequest.getPassword().length() >= 20) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD_LENGTH);
        }

        // 3-1. 키 몸무게 0인지 확인
        if(signupRequest.getHeight() * signupRequest.getWeight() == 0 || signupRequest.getWeight() > 300 || signupRequest.getHeight() > 300){
            throw new CustomException(ErrorCode.INVALID_WEIGHT_AND_HEIGHT);
        }

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
        // 5. user 저장
        userRepository.save(user);
        // 6. myHealth 저장
        MyHealth myHealth = MyHealth.createMyHealth(user);
        myHealthRepository.save(myHealth);

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


    public CodeCheckResponse codeCheck(CodeCheckRequest request) {
        // 1. redis에서 값 가져오기
        String codeExpirationTime = redisService.getEmailExpirationTime(request.getEmail());
        String codeAnswer = redisService.getEmailCode(request.getEmail());

        if (codeExpirationTime == null) {
            throw new CustomException(ErrorCode.CODE_NOT_FOUND);
        }

        // 2. 유효기간 확인
        LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isAfter(LocalDateTime.parse(codeExpirationTime))) {
            throw new CustomException(ErrorCode.CODE_EXPIRED);
        }

        // 3. 코드 일치 여부 확인
        CodeCheckResponse response = CodeCheckResponse.createCodeCheckResponse();
        if (codeAnswer.equals(request.getCode())) {
            response.setIsAvailable();
        }

        return response;
    }

    @Async
    @Retryable(value = { EmailServerException.class, RedisConnectionFailureException.class },
            backoff = @Backoff(delay = 2000))
    //전송 실패시 3번 재시도
    public void sendCode(String email){
        // 1. 인증 코드 생성
        int code = makeRandomCode();

        // 2. 이메일 전송
        String expirationTime = emailService.sendCodeEmail(email, code);

        // 3. 레디스 저장
        redisService.saveEmailCode(email, String.valueOf(code), expirationTime);
    }

    // 재시도 실패 시 복구 로직
    @Recover
    public void recoverFromFailure(Exception e, String email) {
        // 재시도 실패 시 처리할 로직
        log.warn("이메일 전송 실패");
        throw new CustomException(ErrorCode.FAILED_SEND_EMAIL);
    }

    public void resetPassword(ResetPasswordRequest request){
        // 1. 유저 찾기
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 2. 생일 확인
        LocalDate inputBirth = request.getBirth();
        if(!inputBirth.equals(user.getBirth())){
            throw new CustomException(ErrorCode.BIRTH_INVALID);
        }

        // 3. 패스워드 재생성
        String password = PasswordGenerator.generateRandomPassword();

        // 4. 이메일 전송
        emailService.sendPasswordEmail(request.getEmail(), password);

        // 5. user DB 저장
        user.updateRandomPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public void logout(int userId){
        redisService.removeRefreshToken(userId);
    }

    public ReissueTokenResponse reissueAccessToken(HttpServletRequest request){

        // 1. 쿠키에서 refreshToken 재발급
        String token = jwtService.extractRefreshToken(request);

        // 2. 유효성 검증
        if(!jwtService.validateToken(token)){
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 3. userId 찾기
        int userId = jwtService.getUserId(token);

        // 4. refreshToken 일치 확인
        String redisToken = redisService.getRefreshToken(userId);

        if(!redisToken.equals(token)){
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 5. accessToken 재발급
        String accessToken = jwtService.buildAccessToken(userId);

        return ReissueTokenResponse.createReissueTokenResponse(accessToken);
    }


    // 랜덤 인증번호 생성
    private int makeRandomCode(){
        Random random = new Random();
        String randomNumber = "";

        for (int i = 0; i < 6; i++) {
            randomNumber += random.nextInt(10);
        }
        return Integer.parseInt(randomNumber);
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
