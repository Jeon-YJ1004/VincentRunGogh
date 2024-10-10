package com.vincentrungogh.domain.drawing.service.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SaveDrawingResponse {

    private String drawingImage;
    private String drawingDetailImage;

    @Builder
    private SaveDrawingResponse(String drawingImage, String drawingDetailImage){
        this.drawingImage = drawingImage;
        this.drawingDetailImage = drawingDetailImage;
    }

    public static SaveDrawingResponse createSaveDrawingResponse(String drawingImage, String drawingDetailImage){
        return SaveDrawingResponse
                .builder()
                .drawingImage(drawingImage)
                .drawingDetailImage(drawingDetailImage)
                .build();
    }
}
