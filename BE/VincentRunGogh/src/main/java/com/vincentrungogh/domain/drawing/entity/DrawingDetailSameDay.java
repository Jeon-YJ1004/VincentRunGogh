package com.vincentrungogh.domain.drawing.entity;

// 드로잉 디테일 상세 정보 조회에서 두 번째 QueryDSL을 받기 위한 클래스

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class DrawingDetailSameDay {
    private String drawingDetailId;
    private String drawingDetailImage;
    private int drawingDetailDistance;
    private int drawingDetailTime;
    private double drawingDetailSpeed;
    private LocalDateTime drawingDetailCreateTime;
}
