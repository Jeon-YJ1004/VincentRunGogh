package com.vincentrungogh.domain.drawing.entity;

// 드로잉 활동 정보 조회에서 첫 번째 QueryDSL을 받기 위한 클래스

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class EachMonthWalkDistanceTime {
    private String whatYearAndMonth;
    private int monthStep;
    private int monthDistance;
    private int monthTime;
}
