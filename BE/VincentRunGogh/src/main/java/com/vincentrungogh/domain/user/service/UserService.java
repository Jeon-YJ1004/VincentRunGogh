package com.vincentrungogh.domain.user.service;

import com.vincentrungogh.domain.drawing.entity.Drawing;
import com.vincentrungogh.domain.drawing.entity.DrawingDetail;
import com.vincentrungogh.domain.drawing.repository.DrawingDetailRepository;
import com.vincentrungogh.domain.drawing.repository.DrawingRepository;
import com.vincentrungogh.domain.user.entity.User;
import com.vincentrungogh.domain.user.repository.UserRepository;
import com.vincentrungogh.domain.user.service.dto.request.UpdateUserProfileRequest;
import com.vincentrungogh.domain.user.service.dto.response.UserProfileResponse;
import com.vincentrungogh.domain.user.service.dto.response.WeekExerciseResponse;
import com.vincentrungogh.global.auth.service.dto.response.UserPrincipal;
import com.vincentrungogh.global.exception.CustomException;
import com.vincentrungogh.global.exception.ErrorCode;
import com.vincentrungogh.global.service.AwsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final DrawingRepository drawingRepository;
    private final DrawingDetailRepository drawingDetailRepository;
    private final AwsService awsService;
    private final PasswordEncoder passwordEncoder;

    public UserProfileResponse getUserProfile(int userId){

        // 1. 유저 찾기
        User user = getUserById(userId);

        // 2. 반환
        return UserProfileResponse.createUserProfileResponse(user.getNickname(), user.getGender(),
                String.valueOf(user.getBirth()), user.getProfile(), user.getHeight(), user.getWeight());

    }

    public void updateUserProfile(int userId, UpdateUserProfileRequest request){
        // 1. 키 몸무게 0인지 확인
        if(request.getHeight() * request.getWeight() == 0){
            throw new CustomException(ErrorCode.INVALID_WEIGHT_AND_HEIGHT);
        }

        // 2. 닉네임 중복 확인
        Optional<User> optionalUser = userRepository.findByNickname(request.getNickname());

        if(optionalUser.isPresent() && optionalUser.get().getId() != userId){
            throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);
        }

        // 3. 유저 찾기
        User user = getUserById(userId);

        // 4. 저장
        user.updateProfile(request.getNickname(), request.getWeight(), request.getHeight());
        userRepository.save(user);
    }

    public void updateProfileImage(int userId, MultipartFile image){
        // 1. aws 저장
        String awsImage = awsService.uploadFile(image, userId);

        // 2. 유저 찾기
        User user = getUserById(userId);

        // 3. aws url 가져오기
        String url = awsService.getImageUrl(awsImage);

        // 4. DB 저장
        user.updateProfileImage(url);
        userRepository.save(user);
    }

    public void updatePassword(int userId, String rawPassword){

        // 0. 비밀번호 길이 확인
        if(rawPassword.length() >= 20) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD_LENGTH);
        }

        // 1. 비밀번호 암호화
        String password = passwordEncoder.encode(rawPassword);

        // 2. 유저 찾기
        User user = getUserById(userId);

        // 3. DB 저장
        user.updatePassword(password);
        userRepository.save(user);
    }

    public WeekExerciseResponse getWeekExercise(int userId) {
        // 0. 유저 찾기
        User user = getUserById(userId);

        // 1. 현재 날짜
        LocalDate date = LocalDate.now();

        // 2. 시작 날짜
        LocalDate startDate = date.minusDays(6);


        // 3. 드로잉 정보 가져오기
        // 4. 드로잉 디테일 가져오기
        List<DrawingDetail> weekDrawingsDetail = drawingDetailRepository.findAllByUserAndCreatedBetweenDates(
                user, startDate, date
        );

        // 5.일주일 정보 리스트 생성
        int[] distance = new int[7];
        int[] time = new int[7];

        // 6. 저장
        for (DrawingDetail drawingDetail : weekDrawingsDetail) {
            int index = (int) ChronoUnit.DAYS.between(drawingDetail.getCreated().toLocalDate(), date);

            distance[6 - index] += drawingDetail.getDistance();
            time[6 - index] += drawingDetail.getTime();
        }

        return WeekExerciseResponse.createWeekExerciseResponse(distance, time);
    }


    public User getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try{
            // 1. 사용자 확인
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            return UserPrincipal.createUserPrincipal(user.getId(), user.getEmail(), user.getPassword());
        } catch (Exception e){
            log.info("loadUserByUsername: " + e.getMessage());
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
    }
}
