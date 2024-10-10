package com.vincentrungogh.domain.drawing.repository;

import com.vincentrungogh.domain.drawing.entity.DrawingTitleArtImage;
import com.vincentrungogh.domain.drawing.entity.EachMonthRouteFreeCount;
import com.vincentrungogh.domain.user.entity.User;

import java.util.List;

public interface DrawingRepositoryCustom {
    List<EachMonthRouteFreeCount> findRouteFreeCountByYearEachMonth(User user, int year);
}
