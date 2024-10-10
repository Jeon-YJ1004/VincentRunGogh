package com.vincentrungogh.domain.board.service.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SaveBoardRequestDto {
    private String comment;
    private int drawingId;
}
