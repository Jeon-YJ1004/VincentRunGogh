package com.vincentrungogh.domain.drawing.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vincentrungogh.domain.drawing.entity.DrawingTitleArtImage;
import com.vincentrungogh.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.vincentrungogh.domain.drawing.entity.QDrawing.*;
import static com.vincentrungogh.domain.route.entity.QRoute.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DrawingRepositoryImpl implements DrawingRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public DrawingTitleArtImage findTitleAndArtImageById(int drawingId) {
        return queryFactory
                .select(Projections.constructor(DrawingTitleArtImage.class,
                        drawing.title.as("title"),
                        route.artImage.as("routeImage")
                        ))
                .from(drawing)
                .join(drawing.route, route)
                .where(drawing.id.eq(drawingId))
                .fetchOne();
    }

}
