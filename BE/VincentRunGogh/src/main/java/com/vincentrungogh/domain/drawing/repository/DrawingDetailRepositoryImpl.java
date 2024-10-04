package com.vincentrungogh.domain.drawing.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanTemplate;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vincentrungogh.domain.drawing.entity.Drawing;
import com.vincentrungogh.domain.drawing.entity.DrawingDetailGroup;
import com.vincentrungogh.domain.drawing.entity.QDrawing;
import com.vincentrungogh.domain.drawing.entity.DrawingDetail;
import com.vincentrungogh.domain.drawing.entity.QDrawingDetail;
import com.vincentrungogh.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.vincentrungogh.domain.drawing.entity.QDrawingDetail.*;
import static com.vincentrungogh.domain.drawing.entity.QDrawing.*;


@Slf4j
@Repository
@RequiredArgsConstructor
public class DrawingDetailRepositoryImpl implements DrawingDetailRepositoryCustom {

    private final JPAQueryFactory queryFactory;

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
    public List<DrawingDetailGroup> findAllByUserGroupBySameDay(User user, int year, int month) {
        /** where절에서 사용 : 파라미터로 전달된 int year, int month로 LocalDateTime 만들기 */
        // 해당 연월의 가장 처음 시점 (첫날 00:00:00)
        LocalDateTime startOfMonth = LocalDateTime.of(LocalDate.of(year, month, 1), LocalTime.MIN);
        // 해당 연월의 가장 마지막 시점 (마지막 날 23:59:59)
        LocalDateTime endOfMonth = LocalDateTime.of(LocalDate.of(year, month, 1), LocalTime.MAX).with(TemporalAdjusters.lastDayOfMonth());
        /** groupBy절에서 사용 : 연도-월-일 기준으로 LocalDateTime 필드에서 추출하여 그룹화 */
        DateTemplate<String> dateGroupBy = Expressions.dateTemplate(String.class, "DATE_FORMAT({0}, '%Y-%m-%d')", drawingDetail.created);
        /** isCompleted 필드를 기준으로 true가 하나라도 있는지 체크하는 BooleanTemplate */
        BooleanTemplate isCompletedGroupBy = Expressions.booleanTemplate("MAX(CASE WHEN {0} = true THEN 1 ELSE 0 END) = 1", drawingDetail.isCompleted);
        return queryFactory
                .select(Projections.constructor(DrawingDetailGroup.class,
                        drawing.id.as("drawingId"),
                        drawing.title.as("drawingName"),
                        dateGroupBy.as("created"),
                        drawingDetail.time.sum().as("drawingTime"),
                        drawingDetail.distance.sum().as("drawingDistance"),
                        isCompletedGroupBy.as("isCompleted")  // BooleanTemplate을 적용
                        ))
                .from(drawingDetail)
                .join(drawingDetail.drawing, drawing)
                .where(drawing.user.eq(user)
                        .and(drawingDetail.created.between(startOfMonth, endOfMonth)))
                .groupBy(drawingDetail.drawing, dateGroupBy)
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
