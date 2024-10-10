package com.vincentrungogh.domain.drawing.entity;

// 드로잉 활동 정보 조회에서 두 번째 QueryDSL을 받기 위한 클래스

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class EachMonthRouteFreeCount {
    private String whatYearAndMonth;
    private long monthRouteDrawingCnt;
    private long monthFreeDrawingCnt;
}
