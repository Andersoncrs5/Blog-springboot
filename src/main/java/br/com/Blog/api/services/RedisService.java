package br.com.Blog.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void save(String chave, Object valor, int timeInMinutes) {
        redisTemplate.opsForValue().set(chave, valor, Duration.ofMinutes(timeInMinutes));
    }

    public Object get(String chave) {
        return redisTemplate.opsForValue().get(chave);
    }

    public void delete(String chave) {
        redisTemplate.delete(chave);
    }

    public boolean exists(String chave) {
        return redisTemplate.hasKey(chave);
    }
}
