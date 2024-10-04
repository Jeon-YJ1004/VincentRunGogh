package com.vincentrungogh.domain.drawing.repository;

import com.vincentrungogh.domain.drawing.entity.Drawing;
import com.vincentrungogh.domain.drawing.entity.DrawingDetailGroup;
import com.vincentrungogh.domain.user.entity.User;
import com.vincentrungogh.domain.drawing.entity.DrawingDetail;
import com.vincentrungogh.domain.user.entity.User;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

import java.time.LocalDate;
import java.util.List;

public interface DrawingDetailRepositoryCustom {
    double findByDrawingAverageSpeed(Drawing drawing);

    int findByDrawingSumDistance(Drawing drawing);

    List<String> findAllIdsByDrawing(Drawing drawing);

    List<DrawingDetailGroup> findAllByUserGroupBySameDay(User user, int year, int month);
    List<DrawingDetail> findAllByUserAndCreatedBetweenDates(User user, LocalDate start, LocalDate end);
}
