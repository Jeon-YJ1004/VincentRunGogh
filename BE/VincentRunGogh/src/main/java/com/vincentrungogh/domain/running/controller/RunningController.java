package com.vincentrungogh.domain.running.controller;

import com.vincentrungogh.domain.running.service.RunningService;
import com.vincentrungogh.domain.running.service.dto.request.RunningRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@Slf4j
@Controller
@RequiredArgsConstructor
public class RunningController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RunningService runningService;

    // 달리기 시작
    @MessageMapping("/running/{nickname}")
    public void running(@DestinationVariable String nickname,
                             @Valid RunningRequest request,
                        SimpMessageHeaderAccessor accessor){

        log.info(nickname + " : 달리기");
        int userId = (int) accessor.getSessionAttributes().get("userId");
        runningService.running(request, userId);
    }


}
