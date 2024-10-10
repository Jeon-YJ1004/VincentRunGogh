package com.vincentrungogh.domain.calendar.service.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MonthCalendarResponse {
    private Integer monthTotalTime;
    private Double monthTotalDistance;
    private List<DayCalendarResponse> dayList;

    @Builder
    private MonthCalendarResponse(Integer monthTotalTime, Double monthTotalDistance, List<DayCalendarResponse> dayList) {
        this.monthTotalTime = monthTotalTime;
        this.monthTotalDistance = monthTotalDistance;
        this.dayList = dayList;
    }

    public static MonthCalendarResponse createMonthCalendarResponse(Integer monthTotalTime, Double monthTotalDistance, List<DayCalendarResponse> dayList) {
        return MonthCalendarResponse
                .builder()
                .monthTotalTime(monthTotalTime)
                .monthTotalDistance(monthTotalDistance)
                .dayList(dayList)
                .build();
    }
}
