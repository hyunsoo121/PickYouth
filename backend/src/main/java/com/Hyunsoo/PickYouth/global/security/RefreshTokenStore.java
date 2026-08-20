package com.Hyunsoo.PickYouth.global.security;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis에 email당 refresh token 1개를 저장한다. {@code /refresh} 호출마다 새로 발급된 토큰으로 덮어써 회전(rotation)시키므로, 탈취된
 * 구 토큰은 다음 회전 시점에 바로 재사용이 막힌다.
 */
@Component
public class RefreshTokenStore {

  private static final String KEY_PREFIX = "refresh:";

  private final StringRedisTemplate redisTemplate;
  private final long refreshTokenExpirationMs;

  public RefreshTokenStore(StringRedisTemplate redisTemplate, JwtTokenProvider jwtTokenProvider) {
    this.redisTemplate = redisTemplate;
    this.refreshTokenExpirationMs = jwtTokenProvider.getRefreshTokenExpirationMs();
  }

  public void save(String email, String refreshToken) {
    redisTemplate
        .opsForValue()
        .set(key(email), refreshToken, Duration.ofMillis(refreshTokenExpirationMs));
  }

  public boolean matches(String email, String refreshToken) {
    String stored = redisTemplate.opsForValue().get(key(email));
    return stored != null && stored.equals(refreshToken);
  }

  private String key(String email) {
    return KEY_PREFIX + email;
  }
}
