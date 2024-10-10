package com.vincentrungogh.global.auth.service.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FindDuplicatedResponse {

    private Boolean isDuplicated;

    @Builder
    private FindDuplicatedResponse(Boolean isDuplicated) {
        this.isDuplicated = isDuplicated;
    }

    public static FindDuplicatedResponse createFindDuplicatedResponse(Boolean isDuplicated) {
        return FindDuplicatedResponse.builder()
                .isDuplicated(isDuplicated)
                .build();
    }
}
