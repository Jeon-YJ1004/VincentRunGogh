package com.vincentrungogh.domain.running.service.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RunningRequest {
    @NotNull
    private Double lat;
    @NotNull
    private Double lng;
    @NotBlank
    private String time;

    @Builder
    private RunningRequest(Double lat, Double lng, String time) {
        this.lat = lat;
        this.lng = lng;
        this.time = time;
    }

    public static RunningRequest createRunningRequest(Double lat, Double lng, String time) {
        return RunningRequest.builder().lat(lat).lng(lng).time(time)
                .build();
    }
}
