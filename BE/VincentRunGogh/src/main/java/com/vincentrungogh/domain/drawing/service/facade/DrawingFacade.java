package com.vincentrungogh.domain.drawing.service.facade;

import com.vincentrungogh.domain.drawing.entity.GetDrawingType;
import com.vincentrungogh.domain.drawing.service.DrawingContext;
import com.vincentrungogh.domain.drawing.service.dto.response.DrawingListResponseDto;
import com.vincentrungogh.domain.route.entity.RouteType;
import com.vincentrungogh.domain.user.entity.User;
import com.vincentrungogh.domain.user.service.UserService;
import com.vincentrungogh.global.exception.CustomException;
import com.vincentrungogh.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrawingFacade {

    private final UserService userService;
    private final DrawingContext drawingContext;

    @Transactional
    public DrawingListResponseDto getDrawingList(int userId, String type) {
        //유저 찾기
        User user = userService.getUserById(userId);

        //타입 확인
        //ongoing -> 하고 있는 것
        //done -> 완료된 것
        //community -> 드로잉 완료되었지만 커뮤니티 존재 x
        GetDrawingType getDrawingType;
        try {
            getDrawingType = GetDrawingType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_PARAM_TYPE);
        }

        return drawingContext.findDrawingList(user, getDrawingType);
    }

}
