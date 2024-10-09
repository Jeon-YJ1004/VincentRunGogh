package com.vincentrungogh.domain.drawing.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DrawingDetailsSummary {
    private Integer totalTime;
    private Integer totalStep;
    private Double avgSpeed;
    private Integer totalDistance;
}
