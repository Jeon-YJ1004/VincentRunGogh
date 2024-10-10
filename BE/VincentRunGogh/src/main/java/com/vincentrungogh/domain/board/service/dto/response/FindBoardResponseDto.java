package com.vincentrungogh.domain.board.service.dto.response;

import com.vincentrungogh.domain.board.service.dto.common.FindBoard;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class FindBoardResponseDto {
    private List<FindBoard> boardList;

    @Builder
    private FindBoardResponseDto(List<FindBoard> boardList) {
        this.boardList = boardList;
    }

    public static FindBoardResponseDto createFindBoardResponseDto(List<FindBoard> boardList) {
        return FindBoardResponseDto.builder()
                .boardList(boardList)
                .build();
    }


}
