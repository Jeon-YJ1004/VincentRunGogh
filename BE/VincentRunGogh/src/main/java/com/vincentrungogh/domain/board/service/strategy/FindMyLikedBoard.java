package com.vincentrungogh.domain.board.service.strategy;

import com.vincentrungogh.domain.board.entity.Board;
import com.vincentrungogh.domain.board.entity.UserLike;
import com.vincentrungogh.domain.board.repository.BoardRepository;
import com.vincentrungogh.domain.board.repository.UserLikeRepository;
import com.vincentrungogh.domain.board.service.dto.common.FindBoard;
import com.vincentrungogh.domain.board.service.dto.response.FindBoardResponseDto;
import com.vincentrungogh.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service("FindMyLikedBoard")
@RequiredArgsConstructor
public class FindMyLikedBoard implements BoardStrategy {

    private final BoardRepository boardRepository;
    private final UserLikeRepository userLikeRepository;

    @Override
    public FindBoardResponseDto findBoard(User user, double lat, double lng) {

        // 내가 좋아요한 게시글 모두 가져와서 반환하기
        List<FindBoard> findBoardList = userLikeRepository.findAllByUser(user).stream()
                .map(userLike -> FindBoard.createFindBoard(userLike.getBoard(), lat, lng))
                .filter(Objects::nonNull)
                .toList();

        return FindBoardResponseDto.createFindBoardResponseDto(findBoardList);
    }

}
