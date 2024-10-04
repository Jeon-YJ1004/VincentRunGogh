package com.vincentrungogh.domain.drawing.repository;

import com.vincentrungogh.domain.drawing.entity.Drawing;
import com.vincentrungogh.domain.drawing.entity.DrawingDetail;
import com.vincentrungogh.domain.drawing.entity.DrawingDetailGroup;
import com.vincentrungogh.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DrawingDetailRepository extends JpaRepository<DrawingDetail, Integer>, DrawingDetailRepositoryCustom {
    List<DrawingDetail> findAllByDrawingAndCreatedBetween(Drawing drawing, LocalDateTime start, LocalDateTime end);
    List<DrawingDetail> findAllByDrawing(Drawing drawing);

    @Override
    double findByDrawingAverageSpeed(Drawing drawing);

    @Override
    int findByDrawingSumDistance(Drawing drawing);

    @Override
    List<DrawingDetailGroup> findAllByUserGroupBySameDay(User user, int year, int month);
}
