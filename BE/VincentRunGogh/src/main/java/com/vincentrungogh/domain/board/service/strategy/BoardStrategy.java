package com.vincentrungogh.domain.board.service.strategy;

import com.vincentrungogh.domain.board.service.dto.response.FindBoardResponseDto;
import com.vincentrungogh.domain.user.entity.User;

public interface BoardStrategy {
    FindBoardResponseDto findBoard(User user, double lat, double lng);
}
