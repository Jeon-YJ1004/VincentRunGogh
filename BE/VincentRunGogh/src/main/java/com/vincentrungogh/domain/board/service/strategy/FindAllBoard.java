package com.vincentrungogh.domain.board.service.strategy;

import com.vincentrungogh.domain.board.entity.Board;
import com.vincentrungogh.domain.board.repository.BoardRepository;
import com.vincentrungogh.domain.board.service.dto.common.FindBoard;
import com.vincentrungogh.domain.board.service.dto.response.FindBoardResponseDto;
import com.vincentrungogh.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service("FindAllBoard")
@RequiredArgsConstructor
public class FindAllBoard implements BoardStrategy {

    private final BoardRepository boardRepository;

    @Override
    public FindBoardResponseDto findBoard(User user, double lat, double lng) {

        // 모든 게시글 sql에서 조회하기
        List<Board> boardList = boardRepository.findAll();

        log.info("boardList "+ boardList);

        List<FindBoard> findBoardList = boardList.stream()
                .map(board -> FindBoard.createFindBoard(board, lat, lng))
                .filter(Objects::nonNull) // null 값 제거
                .toList();

        log.info("findBoardList "+ findBoardList);

        return FindBoardResponseDto.createFindBoardResponseDto(findBoardList);

    }


}
