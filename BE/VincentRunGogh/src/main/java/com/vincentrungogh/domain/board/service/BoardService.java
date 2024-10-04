package com.vincentrungogh.domain.board.service;


import com.vincentrungogh.domain.board.entity.Board;
import com.vincentrungogh.domain.board.repository.BoardRepository;

import com.vincentrungogh.domain.route.entity.Route;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    // 게시글을 mysql에 저장
    // String comment, Route route
    public Board saveBoard(String comment, Route route){
        Board board = Board.createBoard(comment, route);
        boardRepository.save(board);
        return board;
    }

}
