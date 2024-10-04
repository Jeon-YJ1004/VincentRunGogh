package com.vincentrungogh.domain.board.service.strategy;

import com.vincentrungogh.domain.board.entity.Board;
import com.vincentrungogh.domain.board.repository.BoardRepository;
import com.vincentrungogh.domain.board.service.dto.common.FindBoard;
import com.vincentrungogh.domain.board.service.dto.response.FindBoardResponseDto;
import com.vincentrungogh.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service("FindMineBoard")
@RequiredArgsConstructor
public class FindMineBoard implements BoardStrategy {

    private final BoardRepository boardRepository;

    @Override
    public FindBoardResponseDto findBoard(User user, double lat, double lng) {

        // 내가 쓴 게시글 sql에서 조회하기
        List<Board> boardList = boardRepository.findByRouteUser(user);

        List<FindBoard> findBoardList = boardList.stream()
                .map(board -> FindBoard.createFindBoard(board, lat, lng))
                .filter(Objects::nonNull)
                .toList();

        return FindBoardResponseDto.createFindBoardResponseDto(findBoardList);
    }

}
