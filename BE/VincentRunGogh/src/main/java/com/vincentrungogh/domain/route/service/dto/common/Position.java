package com.vincentrungogh.domain.route.service.dto.common;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class Position {
    private double lat;
    private double lng;
}
