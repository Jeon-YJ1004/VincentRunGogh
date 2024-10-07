package com.vincentrungogh.domain.drawing.repository;

import com.vincentrungogh.domain.drawing.entity.Drawing;
import com.vincentrungogh.domain.drawing.entity.DrawingTitleArtImage;
import com.vincentrungogh.domain.drawing.entity.EachMonthRouteFreeCount;
import com.vincentrungogh.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DrawingRepository extends JpaRepository<Drawing, Integer>, DrawingRepositoryCustom {

    List<Drawing> findAllByUser(User user);

    List<Drawing> findAllByUserAndIsCompletedAndTitleIsNotNull(User user, Boolean isCompleted);

    List<Drawing> findAllByUserAndIsCreatedBoard(User user, Boolean isCreatedBoard);

    int countAllByUserAndIsCompletedAndTitleIsNotNull(User user, Boolean isCompleted);

    @Override
    DrawingTitleArtImage findTitleAndArtImageById(int drawingId);

    @Override
    List<EachMonthRouteFreeCount> findRouteFreeCountByYearEachMonth(User user, int year);
}
