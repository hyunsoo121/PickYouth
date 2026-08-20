package com.Hyunsoo.PickYouth.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  private final SecretKey key;
  private final long accessTokenExpirationMs;
  private final long refreshTokenExpirationMs;

  public JwtTokenProvider(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.access-token-expiration-ms}") long accessTokenExpirationMs,
      @Value("${jwt.refresh-token-expiration-ms}") long refreshTokenExpirationMs) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.accessTokenExpirationMs = accessTokenExpirationMs;
    this.refreshTokenExpirationMs = refreshTokenExpirationMs;
  }

  public String generateAccessToken(String email) {
    return generateToken(email, accessTokenExpirationMs);
  }

  public String generateRefreshToken(String email) {
    return generateToken(email, refreshTokenExpirationMs);
  }

  public long getRefreshTokenExpirationMs() {
    return refreshTokenExpirationMs;
  }

  /**
   * {@code jti}에 랜덤 UUID를 넣어 매 호출마다 토큰이 달라지게 한다 — {@code sub}/{@code iat}/{@code exp}만으로 서명하면 같은
   * 초(second) 안에 발급된 토큰이 바이트 단위로 동일해져(JWT 자체엔 난수 요소가 없음), refresh 회전 시 "새로 발급된 토큰이 곧 이전 토큰과 같아져 버려
   * 무효화가 안 되는" 문제가 실측으로 발견됨.
   */
  private String generateToken(String email, long expirationMs) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + expirationMs);

    return Jwts.builder()
        .subject(email)
        .id(UUID.randomUUID().toString())
        .issuedAt(now)
        .expiration(expiry)
        .signWith(key)
        .compact();
  }

  public String getEmail(String token) {
    return parseClaims(token).getSubject();
  }

  public boolean isValid(String token) {
    try {
      parseClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  private Claims parseClaims(String token) {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
  }
}
