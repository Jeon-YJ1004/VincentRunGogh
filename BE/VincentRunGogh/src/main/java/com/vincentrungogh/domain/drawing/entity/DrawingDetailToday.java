package com.vincentrungogh.domain.drawing.entity;

// 마이헬스 정보 조회에서 QueryDSL를 받기 위한 클래스

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class DrawingDetailToday {
    private int todayRuntime;
    private int todayDistance;
    private double todayAvgSpeed;
    private int todayStep;
}
