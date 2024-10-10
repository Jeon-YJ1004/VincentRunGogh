package com.vincentrungogh.domain.drawing.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;

// 월별 드로잉 데이터 조회에서 QueryDSL를 받기 위한 클래스

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class DrawingDetailGroup {
    private int drawingId;
    private String drawingName;
    private String created;
    private int drawingTime;
    private int drawingDistance;
    private Boolean isCompleted;
}
//    @Builder
//    private DrawingDetailGroup(Integer drawingId, String drawingName, Integer drawingTime, Double drawingDistance, LocalDateTime created) {
//        this.drawingId = drawingId;
//        this.drawingName = drawingName;
//        this.drawingTime = drawingTime;
//        this.drawingDistance = drawingDistance;
//        this.created = created;
//    }
//
//    public static DrawingDetailGroup createDrawingDetailGroup(Integer drawingId, String drawingName, Integer drawingTime, Double drawingDistance, LocalDateTime created) {
//        return DrawingDetailGroup
//                .builder()
//                .drawingId(drawingId)
//                .drawingName(drawingName)
//                .drawingTime(drawingTime)
//                .drawingDistance(drawingDistance)
//                .created(created)
//                .build();
//    }

