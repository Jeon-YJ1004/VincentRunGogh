package com.vincentrungogh.domain.drawing.service.dto.common;

import com.vincentrungogh.domain.drawing.entity.Drawing;
import com.vincentrungogh.domain.route.entity.Route;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FindDrawing {
    private int drawingId;
    private String title;
    private String artImage;
    private String drawingImage;
    private LocalDateTime updated;

    @Builder
    private FindDrawing(int drawingId, String title, String artImage, String drawingImage, LocalDateTime updated) {
        this.drawingId = drawingId;
        this.title = title;
        this.artImage = artImage;
        this.drawingImage = drawingImage;
        this.updated = updated;
    }

    public static FindDrawing createFindDrawing(Drawing drawing, Optional<Route> route) {

        String artImage = null;

        if (route.isPresent()) {
            artImage = route.get().getArtImage();
        }

        return FindDrawing.builder()
                .drawingId(drawing.getId())
                .title(drawing.getTitle())
                .artImage(artImage)
                .drawingImage(drawing.getAccumulatedDrawingImage())
                .updated(drawing.getUpdated())
                .build();
    }

}
