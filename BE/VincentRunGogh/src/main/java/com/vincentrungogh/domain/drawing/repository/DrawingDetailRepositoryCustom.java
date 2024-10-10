package com.vincentrungogh.domain.drawing.repository;

import com.vincentrungogh.domain.drawing.entity.*;
import com.vincentrungogh.domain.user.entity.User;
import com.vincentrungogh.domain.drawing.entity.DrawingDetail;

import java.time.LocalDate;
import java.util.List;

public interface DrawingDetailRepositoryCustom {
    double findByDrawingAverageSpeed(Drawing drawing);

    int findByDrawingSumDistance(Drawing drawing);

    List<String> findAllIdsByDrawing(Drawing drawing);

    List<DrawingDetailGroup> findAllByUserGroupBySameDay(User user, int year, int month);

    List<DrawingDetail> findAllByUserAndCreatedBetweenDates(User user, LocalDate start, LocalDate end);

    List<DrawingDetailSameDay> findByDrawingIdAndDay(User user, int drawingId, String date);

    Long countAllByDrawings(List<Drawing> drawings);

    List<EachMonthWalkDistanceTime> findWalkDistanceTimeByYearEachMonth(User user, int year);

    DrawingDetailToday findTodayByUser(User user);

    DrawingDetailsSummary findDrawingDetailsSummary(Drawing drawing);
}
