package com.vincentrungogh.domain.drawing.service.dto.request;

import com.vincentrungogh.domain.running.service.dto.request.RunningRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReCompleteDrawingRequest {
    @NotBlank
    private String drawingImage;
    @NotBlank
    private String drawingDetailImage;
    @NotNull
    private Integer step;
    @NotBlank
    private String title;
    @NotNull
    private List<RunningRequest> positions;
}
