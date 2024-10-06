package com.vincentrungogh.domain.drawing.repository;

import com.vincentrungogh.domain.drawing.entity.DrawingTitleArtImage;
import com.vincentrungogh.domain.user.entity.User;

public interface DrawingRepositoryCustom {
    DrawingTitleArtImage findTitleAndArtImageById(int drawingId);
}
