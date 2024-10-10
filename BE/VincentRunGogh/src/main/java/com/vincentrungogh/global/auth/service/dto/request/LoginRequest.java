package com.vincentrungogh.global.auth.service.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "모든 항목을 입력해주세요.")
    private String email;
    @NotBlank(message = "모든 항목을 입력해주세요.")
    private String password;
}
