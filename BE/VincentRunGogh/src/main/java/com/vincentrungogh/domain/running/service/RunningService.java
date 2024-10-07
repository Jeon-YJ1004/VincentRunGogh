package com.vincentrungogh.domain.running.service;

import com.vincentrungogh.domain.running.service.dto.request.RunningRequest;
import com.vincentrungogh.global.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RunningService {

    private final RedisService redisService;

    public void running(RunningRequest request, int userId){
        // 레디스 저장
        log.info(String.valueOf(request));
        redisService.saveRunning(userId, request);

    }
}