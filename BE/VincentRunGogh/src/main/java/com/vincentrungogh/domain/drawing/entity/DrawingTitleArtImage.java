package com.vincentrungogh.domain.drawing.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

// 드로잉 디테일 상세 정보 조회에서 첫 번째 QueryDSL을 받기 위한 클래스

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class DrawingTitleArtImage {
    private String title;
    private String routeImage;
}
