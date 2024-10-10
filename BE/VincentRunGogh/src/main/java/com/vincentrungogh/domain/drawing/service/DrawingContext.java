package com.vincentrungogh.domain.drawing.service;


import com.vincentrungogh.domain.drawing.entity.GetDrawingType;
import com.vincentrungogh.domain.drawing.service.dto.response.DrawingListResponseDto;
import com.vincentrungogh.domain.drawing.service.strategy.DrawingStrategy;
import com.vincentrungogh.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DrawingContext {

    private final Map<String, DrawingStrategy> drawingStrategyMap;

    public DrawingListResponseDto findDrawingList(User user, GetDrawingType type) {
        //타입 선정
        DrawingStrategy drawingStrategy = drawingStrategyMap.get(type.getTypeName());

        return drawingStrategy.findDrawingList(user);
    }
}
