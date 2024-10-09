package com.vincentrungogh.domain.drawing.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vincentrungogh.domain.drawing.entity.DrawingTitleArtImage;
import com.vincentrungogh.domain.drawing.entity.EachMonthRouteFreeCount;
import com.vincentrungogh.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.vincentrungogh.domain.drawing.entity.QDrawing.*;
import static com.vincentrungogh.domain.drawing.entity.QDrawingDetail.drawingDetail;
import static com.vincentrungogh.domain.route.entity.QRoute.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DrawingRepositoryImpl implements DrawingRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<EachMonthRouteFreeCount> findRouteFreeCountByYearEachMonth(User user, int year) {
        /** where절에서 사용 : 파라미터로 전달된 int year로 LocalDateTime 만들기 */
        // 해당 연도의 가장 처음 시점 (1월 1일 00:00:00)
        LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0, 0);
        // 해당 연도의 가장 마지막 시점 (12월 31일 23:59:59)
        LocalDateTime endOfYear = LocalDateTime.of(year, 12, 31, 23, 59, 59);
        /** groupBy절에서 사용 : 연도-월 기준으로 LocalDateTime 필드에서 추출하여 그룹화 */
        DateTemplate<String> monthGroupBy = Expressions.dateTemplate(String.class, "DATE_FORMAT({0}, '%Y-%m')", drawing.updated);
        return queryFactory
                .select(Projections.constructor(EachMonthRouteFreeCount.class,
                        monthGroupBy.as("whatYearAndMonth"),
                        drawing.route.count().as("monthRouteDrawingCnt"),
                        drawing.count().subtract(drawing.route.count()).as("monthFreeDrawingCnt")
                        ))
                .from(drawing)
                .where(drawing.user.eq(user)
                        .and(drawing.isCompleted.eq(true))
                        .and(drawing.updated.between(startOfYear, endOfYear))
                        .and(drawing.title.isNotNull()))
                .groupBy(monthGroupBy)
                .fetch();
    }

}
