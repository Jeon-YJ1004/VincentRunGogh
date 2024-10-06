package com.vincentrungogh.domain.board.service.strategy;

import com.vincentrungogh.domain.board.entity.Board;
import com.vincentrungogh.domain.board.repository.BoardRepository;
import com.vincentrungogh.domain.board.repository.UserLikeRepository;
import com.vincentrungogh.domain.board.service.dto.common.FindBoard;
import com.vincentrungogh.domain.board.service.dto.response.FindBoardResponseDto;
import com.vincentrungogh.domain.user.entity.User;
import com.vincentrungogh.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service("FindMineBoard")
@RequiredArgsConstructor
public class FindMineBoard implements BoardStrategy {

    private final BoardRepository boardRepository;
    private final UserLikeRepository userLikeRepository;

    @Override
    public FindBoardResponseDto findBoard(User user, double lat, double lng) {

        // 내가 쓴 게시글 sql에서 조회하기
        List<Board> boardList = boardRepository.findByRouteUser(user);

        List<FindBoard> findBoardList = boardList.stream()
                .filter(board -> !board.getIsDelete())
                .map(board -> {
                    boolean isLiked = userLikeRepository.findByUserAndBoard(user, board).isPresent();
                    return FindBoard.createFindBoard(board, lat, lng, isLiked);
                })
                .filter(Objects::nonNull) // null 값 제거
                .toList();

        return FindBoardResponseDto.createFindBoardResponseDto(findBoardList);
    }

}
