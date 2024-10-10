package com.vincentrungogh.global.auth.service.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginResponse {

    private String accessToken;
    private Boolean isChange;
    private String nickname;
    private String profile;

    @Builder
    private LoginResponse(String accessToken, Boolean isChange, String nickname, String profile) {
        this.accessToken = accessToken;
        this.isChange = isChange;
        this.nickname = nickname;
        this.profile = profile;
    }

    public static LoginResponse createLoginResponse(String accessToken, Boolean isChange, String nickname, String profile) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .isChange(isChange)
                .nickname(nickname)
                .profile(profile)
                .build();
    }
}
