package com.vincentrungogh.domain.drawing.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;

// QueryDSL를 받기 위한 클래스를 이렇게 만들게 되면, 굳이 response DTO에서 DrawingLastDayResponse가 필요한지 의문.
// 추후에 DrawingLastDayResponse class를 삭제하도록 하자. -> 정말로 그럴까?

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

