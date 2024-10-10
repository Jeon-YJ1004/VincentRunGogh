package com.vincentrungogh.domain.board.service;

import com.vincentrungogh.domain.board.entity.BoardType;
import com.vincentrungogh.domain.board.service.dto.response.FindBoardResponseDto;
import com.vincentrungogh.domain.board.service.strategy.BoardStrategy;
import com.vincentrungogh.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardContext {

    private final Map<String, BoardStrategy> getBoardStrategyMap;

    public FindBoardResponseDto findBoard(User user, BoardType boardType, Double lat, Double lng) {

        // 타입 조회
//        log.info("로그의 유저이름 " +user.getEmail());
//        log.info("로그이름 " + boardType.getTypeName());
//
//        log.info("getBoardStrategyMap keys: " + getBoardStrategyMap.keySet());
        BoardStrategy boardStrategy = getBoardStrategyMap.get(boardType.getTypeName());

        // 반환
        return boardStrategy.findBoard(user, lat, lng);
    }
}
