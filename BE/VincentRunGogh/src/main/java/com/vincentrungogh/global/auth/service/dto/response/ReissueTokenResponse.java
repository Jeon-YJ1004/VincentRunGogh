package com.vincentrungogh.global.auth.service.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReissueTokenResponse {
    private String accessToken;

    @Builder
    private ReissueTokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public static ReissueTokenResponse createReissueTokenResponse(String accessToken) {
        return ReissueTokenResponse
                .builder()
                .accessToken(accessToken)
                .build();
    }
}
