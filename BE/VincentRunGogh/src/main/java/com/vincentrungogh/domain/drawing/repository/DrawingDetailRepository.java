package com.vincentrungogh.domain.drawing.repository;

import com.vincentrungogh.domain.drawing.entity.*;
import com.vincentrungogh.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DrawingDetailRepository extends JpaRepository<DrawingDetail, Integer>, DrawingDetailRepositoryCustom {
    List<DrawingDetail> findAllByDrawingAndCreatedBetween(Drawing drawing, LocalDateTime start, LocalDateTime end);
    List<DrawingDetail> findAllByDrawing(Drawing drawing);
    List<DrawingDetail> findAllByDrawingOrderByCreatedDesc(Drawing drawing);

    @Override
    Long countAllByDrawings(List<Drawing> drawings);

    @Override
    double findByDrawingAverageSpeed(Drawing drawing);

    @Override
    int findByDrawingSumDistance(Drawing drawing);

    @Override
    List<DrawingDetailGroup> findAllByUserGroupBySameDay(User user, int year, int month);

    @Override
    List<DrawingDetailSameDay> findByDrawingIdAndDay(User user, int drawingId, String date);

    @Override
    List<EachMonthWalkDistanceTime> findWalkDistanceTimeByYearEachMonth(User user, int year);

    @Override
    DrawingDetailToday findTodayByUser(User user);

    @Override
    DrawingDetailsSummary findDrawingDetailsSummary(Drawing drawing);
}
