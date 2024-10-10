package com.vincentrungogh.domain.drawing.entity;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class DrawingDetailsSummary {
    private Integer totalTime;
    private Integer totalDistance;
    private Double avgSpeed;
    private Integer totalStep;
}
