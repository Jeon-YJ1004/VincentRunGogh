package com.vincentrungogh.domain.drawing.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vincentrungogh.domain.drawing.entity.Drawing;
import com.vincentrungogh.domain.drawing.entity.DrawingDetail;
import com.vincentrungogh.domain.drawing.entity.QDrawingDetail;
import com.vincentrungogh.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.vincentrungogh.domain.drawing.entity.QDrawing.drawing;

@Repository
@RequiredArgsConstructor
public class DrawingDetailRepositoryImpl implements DrawingDetailRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QDrawingDetail drawingDetail = QDrawingDetail.drawingDetail;

    @Override
    public double findByDrawingAverageSpeed(Drawing drawing) {
        Double avgSpeed = queryFactory.select(drawingDetail.speed.avg())
                .from(drawingDetail)
                .where(drawingDetail.drawing.eq(drawing))
                .fetchOne();

        return Optional.ofNullable(avgSpeed).orElse(0.0);
    }

    @Override
    public int findByDrawingSumDistance(Drawing drawing) {
        Integer distance = queryFactory.select(drawingDetail.distance.sum())
                .from(drawingDetail)
                .where(drawingDetail.drawing.eq(drawing))
                .fetchOne();

        return Optional.ofNullable(distance).orElse(0);
    }

    @Override
    public List<String> findAllIdsByDrawing(Drawing drawing) {
        return queryFactory
                .select(drawingDetail.id)
                .from(drawingDetail)
                .where(drawingDetail.drawing.eq(drawing))
                .fetch();
    }

    @Override
    public List<DrawingDetail> findAllByUserAndCreatedBetweenDates(User user, LocalDate start, LocalDate end) {
        return queryFactory
                .selectFrom(drawingDetail)
                .where(drawingDetail.drawing.in(
                        JPAExpressions
                                .select(drawing)
                                .from(drawing)
                                .where(drawing.user.eq(user)) // user 필터링
                ).and(drawingDetail.created.between(start.atTime(LocalTime.MIN), end.atTime(LocalTime.MAX))))
                .fetch();
    }
}
