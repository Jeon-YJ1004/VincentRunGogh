package com.vincentrungogh.global.service;

import com.vincentrungogh.domain.route.service.dto.common.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String ROOTING_PREFIX = "rooting:";

    public void saveRoutePositionList(int userId, List<Position> positionList) {
        String key = ROOTING_PREFIX + userId;
        redisTemplate.opsForValue().set(key, positionList);
    }

    public List<Position> getRoutePositionList(int userId) {
        String key = ROOTING_PREFIX + userId;
        return (List<Position>) redisTemplate.opsForValue().get(key);
    }
}
