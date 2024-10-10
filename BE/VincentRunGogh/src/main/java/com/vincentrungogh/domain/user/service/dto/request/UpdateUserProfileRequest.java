package com.vincentrungogh.domain.user.service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateUserProfileRequest {

    @NotBlank
    private String nickname;
    @NotNull
    private Double height;
    @NotNull
    private Double weight;
}
