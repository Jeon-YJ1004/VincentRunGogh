package com.vincentrungogh.domain.drawing.service.dto.response;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class DataSaveDrawingDetailResponse {

    private String drawingDetailId;
    private int distance;
    private int time;
    private int speed;
}
