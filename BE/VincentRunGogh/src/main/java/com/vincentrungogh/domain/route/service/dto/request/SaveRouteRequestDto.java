package com.vincentrungogh.domain.route.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class SaveRouteRequestDto {

    @NotBlank
    @Size(min = 1, max = 10, message = "제목은 1글자 이상 10글자 이하로 입력해주세요.")
    private String title; //루트의 제목

    @NotBlank
    private String artImage;
}
