package com.vincentrungogh.domain.running.handler;

import com.vincentrungogh.global.auth.service.JwtService;
import com.vincentrungogh.global.exception.CustomException;
import com.vincentrungogh.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        log.info("preSend");

        // 1. 토큰 존재 여부 확인
        if(accessor.getCommand() == StompCommand.CONNECT){
            String accessToken = getAccessToken(accessor);
            if(!jwtService.validateToken(accessToken)){
                throw new CustomException(ErrorCode.USER_NOT_FOUND);
            }
        }

        return message;
    }

    @EventListener(SessionConnectedEvent.class)
    public void onApplicationEvent(SessionConnectedEvent event) {
        log.info("onApplicationEvent");
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Message<?> connectMessage = (Message<?>) stompHeaderAccessor.getHeader("simpConnectMessage");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(connectMessage);

        String accessToken = getAccessToken(accessor);

        if(!jwtService.validateToken(accessToken)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        accessor.getSessionAttributes().put("userId", jwtService.getUserId(accessToken));
    }

    private String getAccessToken(StompHeaderAccessor accessor){

        String bearerToken = accessor.getFirstNativeHeader("Authorization");


        if(!(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer "))) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return bearerToken.substring(7);
    }

}
