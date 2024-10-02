package com.vincentrungogh.domain.drawing.service.dto.request;

import com.vincentrungogh.domain.running.service.dto.request.RunningRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DataSaveDrawingDetailRequest {

    private List<RunningRequest> positionTimeList;

    @Builder
    private DataSaveDrawingDetailRequest(List<RunningRequest> positionTimeList) {
        this.positionTimeList = positionTimeList;
    }

    public static DataSaveDrawingDetailRequest createDataSaveDrawingDetailRequset(List<RunningRequest> positionTimeList) {
        return DataSaveDrawingDetailRequest.builder()
                .positionTimeList(positionTimeList)
                .build();
    }
}
