package com.vincentrungogh.global.service;

import com.vincentrungogh.domain.route.service.dto.common.Position;
import com.vincentrungogh.domain.running.service.dto.request.RunningRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void saveRoutePositionList(int userId, List<Position> positionList) {
        String key = "rooting:" + userId;
        redisTemplate.opsForValue().set(key, positionList);
    }

    public void removeRoutePositionList(int userId){
        String key = "rooting:" + userId;
        redisTemplate.delete(key);
    }

    public List<Position> getRoutePositionList(int userId) {
        String key = "rooting:" + userId;
        return (List<Position>) redisTemplate.opsForValue().get(key);
    }

    public void saveEmailCode(String email, String code,  String expirationTime){

        redisTemplate.opsForValue().set(email, code);

        redisTemplate.opsForValue().set(email+ "-expirationTime", expirationTime);

        redisTemplate.expire(email+ "-expirationTime", 180, TimeUnit.SECONDS);
        redisTemplate.expire(email, 180, TimeUnit.SECONDS);

    }

    public String getEmailCode(String email){
        return (String) redisTemplate.opsForValue().get(email);
    }

    public String getEmailExpirationTime(String email){
        String key = email + "-expirationTime";
        return (String) redisTemplate.opsForValue().get(key);
    }

    //
    public void saveRefreshToken(int userId, String refreshToken){
        String key = "refreshToken:" + userId;
        redisTemplate.opsForValue().set(key, refreshToken);
    }

    public String getRefreshToken(int userId){
        String key = "refreshToken:" + userId;
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void removeRefreshToken(int userId){
        String key = "refreshToken:" + userId;
        redisTemplate.delete(key);
    }

    // 드로잉 정보 저장
    public void removeRunning(int userId){
        String key = "running:" + userId;
        redisTemplate.delete(key);
    }

    public void saveRunning(int userId, RunningRequest position){
        String key = "running:" + userId;
        redisTemplate.opsForList().rightPush(key, position);
    }

    public List<RunningRequest> getRunning(int userId){
        String key = "running:" + userId;
        return (List<RunningRequest>) (List<?>)redisTemplate.opsForList().range(key, 0, -1);
    }
}
