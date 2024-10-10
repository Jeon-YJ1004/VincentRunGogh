package com.vincentrungogh.domain.board.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardType {
    ALL("FindAllBoard"),
    MINE("FindMineBoard"),
    MYLIKED("FindMyLikedBoard");

    private final String typeName;
}
