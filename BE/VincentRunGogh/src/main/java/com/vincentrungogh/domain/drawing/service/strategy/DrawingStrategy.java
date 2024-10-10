package com.vincentrungogh.domain.drawing.service.strategy;

import com.vincentrungogh.domain.drawing.service.dto.response.DrawingListResponseDto;
import com.vincentrungogh.domain.user.entity.User;

public interface DrawingStrategy {
    DrawingListResponseDto findDrawingList(User user);
}
