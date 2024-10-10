package com.vincentrungogh.domain.user.service.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfileResponse {
    private String nickname;
    private int gender;
    private String birth;
    private String profile;
    private Double height;
    private Double weight;

    @Builder
    private UserProfileResponse(String nickname, int gender, String birth, String profile, Double height, Double weight) {
        this.nickname = nickname;
        this.gender = gender;
        this.birth = birth;
        this.profile = profile;
        this.height = height;
        this.weight = weight;
    }

    public static UserProfileResponse createUserProfileResponse(String nickname, int gender, String birth, String profile, Double height, Double weight) {
        return UserProfileResponse
                .builder()
                .nickname(nickname)
                .gender(gender)
                .birth(birth)
                .profile(profile)
                .height(height)
                .weight(weight)
                .build();
    }
}
