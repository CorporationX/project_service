package faang.school.projectservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void saveToRedis(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public String getFromRedis(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }
}
