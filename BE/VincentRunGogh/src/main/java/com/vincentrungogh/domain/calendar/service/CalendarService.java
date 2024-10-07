package com.vincentrungogh.domain.calendar.service;

import com.vincentrungogh.domain.calendar.service.dto.response.*;
import com.vincentrungogh.domain.drawing.entity.*;
import com.vincentrungogh.domain.drawing.repository.DrawingDetailRepository;
import com.vincentrungogh.domain.drawing.repository.DrawingRepository;
import com.vincentrungogh.domain.route.entity.Route;
import com.vincentrungogh.domain.user.entity.User;
import com.vincentrungogh.domain.user.repository.UserRepository;
import com.vincentrungogh.global.exception.CustomException;
import com.vincentrungogh.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarService {

    // 필요한 의존성 주입
    private final UserRepository userRepository;
    private final DrawingRepository drawingRepository;
    private final DrawingDetailRepository drawingDetailRepository;

    // 월별 드로잉 데이터 조회
    @Transactional
    public MonthCalendarResponse getCalendar(Integer userId, Integer year, Integer month) {

        // 0. 형 변환 및 MonthCalendarResponse 객체 생성을 위한 field 초기값 세팅
        int calendarYear = year;
        int calendarMonth = month;
        int monthTotalTime = 0;
        double monthTotalDistance = 0.0;
        List<DayCalendarResponse> dayList = new ArrayList<>();

        // 1. 유저 아이디로 유저 엔티티 특정하기
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        // 2. 할당된 유저 엔티티로 QueryDSL
        List<DrawingDetailGroup> drawingDetailGroups = drawingDetailRepository.findAllByUserGroupBySameDay(user, calendarYear, calendarMonth);
        log.info(drawingDetailGroups.toString());

        // 3. HashMap에 DrawingLastDayResponse 저장하기
        Map<String, List<DrawingLastDayResponse>> map = new HashMap<>();

        for (int i = 0; i < drawingDetailGroups.size(); i++) {
            DrawingDetailGroup drawingDetailGroup = drawingDetailGroups.get(i);
            // 생성날짜(String class) 추출 -> key
            String key = drawingDetailGroup.getCreated();
            // DrawingLastDayResponse 생성 -> value
            int drawingId = drawingDetailGroup.getDrawingId();
            String drawingName = drawingDetailGroup.getDrawingName();
            int drawingTime = drawingDetailGroup.getDrawingTime();
            int drawingDistance = drawingDetailGroup.getDrawingDistance();
            double kDrawingDistance = Math.round((drawingDistance / 1000.0) * 100.0) / 100.0;
            Boolean isCompleted = drawingDetailGroup.getIsCompleted();
            DrawingLastDayResponse value = DrawingLastDayResponse.createDrawingLastDayResponse(drawingId, drawingName, drawingTime, kDrawingDistance, isCompleted);
            // 해당 key가 map에 이미 존재한다면 기존 리스트에 값 추가, 없다면 새로운 리스트를 만들어 추가
            if(map.containsKey(key)) {
                map.get(key).add(value);
            } else {
                List<DrawingLastDayResponse> newList = new ArrayList<>();
                newList.add(value);
                map.put(key, newList);
            }
        }

        // 4. 하나의 key, 하나의 DayCalendarResponse
        Set<String> keys = map.keySet();
        for (String key : keys) {
            // "2024-09-30"인 key를 이용해 LocalDate class 객체 생성하기
            LocalDate date = LocalDate.parse(key);
            boolean isDrawing = false;
            int dayTotalTime = 0;
            double dayTotalDistance = 0.0;
            List<DrawingLastDayResponse> drawingList = map.get(key);
            for (int i = 0; i < drawingList.size(); i++) {
                DrawingLastDayResponse drawingLastDayResponse = drawingList.get(i);
                dayTotalTime += drawingLastDayResponse.getDrawingTime();
                dayTotalDistance += drawingLastDayResponse.getDrawingDistance();
                if(drawingLastDayResponse.getIsCompleted()) {
                    isDrawing = true;
                }
            }
            // DayCalendarResponse 객체 생성하기
            DayCalendarResponse dayCalendarResponse = DayCalendarResponse.createDayCalendarResponse(date, true, isDrawing, dayTotalTime, dayTotalDistance, drawingList);
            // MonthCalendarResponse field에 반영
            monthTotalTime += dayCalendarResponse.getDayTotalTime();
            monthTotalDistance += dayCalendarResponse.getDayTotalDistance();
            dayList.add(dayCalendarResponse);
        }

        // 5. MonthCalendarResponse 객체 리턴
        return MonthCalendarResponse.createMonthCalendarResponse(monthTotalTime, monthTotalDistance, dayList);
    }

    // 드로잉 디테일 상세 정보 조회
    @Transactional
    public DrawingDetailListOnSameDayResponse getDrawingDetails(int userId, int drawingId, String date) {

        // 0. DrawingDetailListOnSameDayResponse 객체 생성을 위한 field 초기값 세팅
        String title;
        String routeImage = null;
        List<DrawingDetailForListOnSameDayResponse> drawingDetailList = new ArrayList<>();

        // 1. 유저 아이디로 유저 엔티티 특정하기
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        /**
        // 2-1. 드로잉 아이디로 QueryDSL
        DrawingTitleArtImage drawingTitleArtImage = drawingRepository.findTitleAndArtImageById(drawingId);
        title = drawingTitleArtImage.getTitle();
        routeImage = drawingTitleArtImage.getRouteImage();
         */
        // 2-1. 드로잉 이름, 아트 이미지(자유드로잉일 때는 null) 구하기
        Drawing drawing = drawingRepository.findById(drawingId)
                .orElseThrow(() -> new CustomException(ErrorCode.DRAWING_NOT_FOUND));
        title = drawing.getTitle();
        Route route = drawing.getRoute();
        if(route != null) {
            routeImage = route.getArtImage();
        }

        // 2-2. 할당된 유저 엔티티, 드로잉 아이디, 드로잉 디테일 완료날짜로 QueryDSL
        List<DrawingDetailSameDay> drawingDetailSameDays = drawingDetailRepository.findByDrawingIdAndDay(user, drawingId, date);

        // 3. QueryDSL을 받기 위한 클래스에서 내가 보내야할 Response DTO로 변환하는 작업
        for (DrawingDetailSameDay drawingDetailSameDay : drawingDetailSameDays) {
            String drawingDetailId = drawingDetailSameDay.getDrawingDetailId();
            String drawingDetailImage = drawingDetailSameDay.getDrawingDetailImage();
            int drawingDetailDistance = drawingDetailSameDay.getDrawingDetailDistance();
            double kDrawingDetailDistance = Math.round((drawingDetailDistance / 1000.0) * 100.0) / 100.0;
            int drawingDetailTime = drawingDetailSameDay.getDrawingDetailTime();
            double drawingDetailSpeed = drawingDetailSameDay.getDrawingDetailSpeed();
            int drawingDetailAvgPace;
            if (drawingDetailSpeed > 0.0) {
                drawingDetailAvgPace = (int) Math.round(3600 / drawingDetailSpeed);
            } else {
                drawingDetailAvgPace = 0;
            }
            LocalDateTime drawingDetailCreateTime = drawingDetailSameDay.getDrawingDetailCreateTime();
            drawingDetailList.add(DrawingDetailForListOnSameDayResponse.createDrawingDetailForListOnSameDayResponse(drawingDetailId, drawingDetailImage, kDrawingDetailDistance, drawingDetailTime, drawingDetailAvgPace, drawingDetailCreateTime));
        }

        // 4. DrawingDetailListOnSameDayResponse 객체 리턴
        return DrawingDetailListOnSameDayResponse.createDrawingDetailListOnSameDayResponse(title, routeImage, drawingDetailList);
    }
}
