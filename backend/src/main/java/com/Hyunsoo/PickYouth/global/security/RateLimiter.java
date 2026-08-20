package com.Hyunsoo.PickYouth.global.security;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/** Redis {@code INCR} 기반 고정 윈도우(fixed-window) 카운터로 시도 횟수를 제한한다. */
@Component
public class RateLimiter {

  private final StringRedisTemplate redisTemplate;

  public RateLimiter(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public boolean isBlocked(String key, int maxAttempts) {
    String value = redisTemplate.opsForValue().get(key);
    return value != null && Integer.parseInt(value) >= maxAttempts;
  }

  /** 카운터를 1 증가시키고, 이번에 새로 생성된 키라면 window로 만료시간을 건다. */
  public void recordAttempt(String key, Duration window) {
    Long count = redisTemplate.opsForValue().increment(key);
    if (count != null && count == 1L) {
      redisTemplate.expire(key, window);
    }
  }

  public void reset(String key) {
    redisTemplate.delete(key);
  }
}
