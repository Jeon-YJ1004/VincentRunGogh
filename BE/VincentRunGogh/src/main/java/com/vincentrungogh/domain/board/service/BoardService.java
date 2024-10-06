package com.vincentrungogh.domain.board.service;

import com.vincentrungogh.domain.board.entity.Board;
import com.vincentrungogh.domain.board.entity.UserLike;
import com.vincentrungogh.domain.board.repository.BoardRepository;
import com.vincentrungogh.domain.board.repository.UserLikeRepository;
import com.vincentrungogh.domain.route.entity.Route;
import com.vincentrungogh.domain.user.entity.User;
import com.vincentrungogh.domain.user.service.UserService;
import com.vincentrungogh.global.auth.service.dto.response.UserPrincipal;
import com.vincentrungogh.global.exception.CustomException;
import com.vincentrungogh.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserLikeRepository userLikeRepository;
    private final UserService userService;

    // 게시글을 mysql에 저장
    // String comment, Route route
    public Board saveBoard(String comment, Route route){
        Board board = Board.createBoard(comment, route);
        boardRepository.save(board);
        return board;
    }

    // 게시글 좋아요 구현하기
    public void addLike(UserPrincipal userPrincipal, int boardId){
        User user = userService.getUserById(userPrincipal.getId());
        Board board = boardRepository.findById(boardId);

        // board의 좋아요 개수 추가
        board.addLike();
        // 새 행 추가가 아닌 update도 지원하는 save
        boardRepository.save(board);

        // userLike user-board 관계 행 추가
        UserLike userLike = UserLike.createUsersLike(user, board);
        userLikeRepository.save(userLike);
    }

    // 게시글 좋아요 취소 구현하기
    public void deleteLike(UserPrincipal userPrincipal, int boardId){
        User user = userService.getUserById(userPrincipal.getId());
        Board board = boardRepository.findById(boardId);

        // board의 좋아요 개수 추가
        board.deleteLike();
        // 새 행 추가가 아닌 update도 지원하는 save
        boardRepository.save(board);

        // userLike user-board 관계 행에 있는 user-board 찾고 행 삭제하기
        UserLike userLike = userLikeRepository.findByUserAndBoard(user, board)
                .orElseThrow(() -> new CustomException(ErrorCode.USERLIKE_BOARD_NOT_FOUND));

        userLikeRepository.delete(userLike);
    }

}
