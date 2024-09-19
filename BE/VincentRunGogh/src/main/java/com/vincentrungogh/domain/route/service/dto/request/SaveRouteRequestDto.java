package com.vincentrungogh.domain.route.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class SaveRouteRequestDto {

    @NotBlank
    private String title; //루트의 제목

    @NotBlank
    private String artImage;
}
