package com.vincentrungogh.domain.board.service.strategy;

import com.vincentrungogh.domain.board.entity.Board;
import com.vincentrungogh.domain.board.entity.UserLike;
import com.vincentrungogh.domain.board.repository.BoardRepository;
import com.vincentrungogh.domain.board.repository.UserLikeRepository;
import com.vincentrungogh.domain.board.service.dto.common.FindBoard;
import com.vincentrungogh.domain.board.service.dto.response.FindBoardResponseDto;
import com.vincentrungogh.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service("FindMyLikedBoard")
@RequiredArgsConstructor
public class FindMyLikedBoard implements BoardStrategy {

    private final BoardRepository boardRepository;
    private final UserLikeRepository userLikeRepository;

    @Override
    public FindBoardResponseDto findBoard(User user, double lat, double lng) {

        // 우선 사용자가 좋아요한 userLike 목록 조회하기
        List<Board> likedBoardList = userLikeRepository.findAllByUser(user).stream()
                .map(userLike -> userLike.getBoard())
                .filter(board -> !board.getIsDelete())
                .filter(Objects::nonNull) // null 값 제거
                .toList();

        log.info("likedBoardList: " + likedBoardList);

        // findBoard로 변경하기
        List<FindBoard> findBoardList = likedBoardList.stream()
                .map(board -> {
                            boolean isLiked = true;
                            return FindBoard.createFindBoard(board, lat, lng, isLiked);
                        })
                .filter(Objects::nonNull)
                .toList();

        return FindBoardResponseDto.createFindBoardResponseDto(findBoardList);
    }

}
