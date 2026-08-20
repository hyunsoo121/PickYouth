package com.Hyunsoo.PickYouth.domain.auth.service;

import com.Hyunsoo.PickYouth.domain.auth.dto.LoginRequest;
import com.Hyunsoo.PickYouth.domain.auth.dto.RefreshRequest;
import com.Hyunsoo.PickYouth.domain.auth.dto.SignupRequest;
import com.Hyunsoo.PickYouth.domain.auth.dto.TokenResponse;
import com.Hyunsoo.PickYouth.domain.auth.dto.UserResponse;
import com.Hyunsoo.PickYouth.domain.auth.exception.DuplicateEmailException;
import com.Hyunsoo.PickYouth.domain.auth.exception.InvalidCredentialsException;
import com.Hyunsoo.PickYouth.domain.auth.exception.InvalidRefreshTokenException;
import com.Hyunsoo.PickYouth.domain.auth.exception.TooManyAttemptsException;
import com.Hyunsoo.PickYouth.domain.user.entity.User;
import com.Hyunsoo.PickYouth.domain.user.exception.UserNotFoundException;
import com.Hyunsoo.PickYouth.domain.user.repository.UserRepository;
import com.Hyunsoo.PickYouth.global.security.JwtTokenProvider;
import com.Hyunsoo.PickYouth.global.security.RateLimiter;
import com.Hyunsoo.PickYouth.global.security.RefreshTokenStore;
import java.time.Duration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

  private static final int LOGIN_MAX_ATTEMPTS = 5;
  private static final Duration LOGIN_WINDOW = Duration.ofMinutes(1);
  private static final int SIGNUP_MAX_ATTEMPTS = 5;
  private static final Duration SIGNUP_WINDOW = Duration.ofHours(1);

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenStore refreshTokenStore;
  private final RateLimiter rateLimiter;

  public AuthService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager,
      JwtTokenProvider jwtTokenProvider,
      RefreshTokenStore refreshTokenStore,
      RateLimiter rateLimiter) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtTokenProvider = jwtTokenProvider;
    this.refreshTokenStore = refreshTokenStore;
    this.rateLimiter = rateLimiter;
  }

  @Transactional
  public UserResponse signup(SignupRequest request, String clientIp) {
    // 성공/실패(중복 이메일 등) 관계없이 시도 자체를 카운트한다 — 가입 남용/스팸 방지가 목적이라 실패만 셀 이유가 없다.
    String key = "signup-attempt:" + clientIp;
    if (rateLimiter.isBlocked(key, SIGNUP_MAX_ATTEMPTS)) {
      throw new TooManyAttemptsException();
    }
    rateLimiter.recordAttempt(key, SIGNUP_WINDOW);

    if (userRepository.existsByEmail(request.email())) {
      throw new DuplicateEmailException(request.email());
    }
    User user =
        User.builder()
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .name(request.name())
            .build();
    return UserResponse.from(userRepository.save(user));
  }

  public TokenResponse login(LoginRequest request, String clientIp) {
    // 실패한 시도만 카운트한다 — 비밀번호를 몇 번 틀리다 결국 성공하는 정상 사용자를 막지 않기 위함.
    String key = "login-fail:" + clientIp;
    if (rateLimiter.isBlocked(key, LOGIN_MAX_ATTEMPTS)) {
      throw new TooManyAttemptsException();
    }
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.email(), request.password()));
    } catch (AuthenticationException e) {
      rateLimiter.recordAttempt(key, LOGIN_WINDOW);
      throw new InvalidCredentialsException();
    }
    rateLimiter.reset(key);
    return issueTokens(request.email());
  }

  public TokenResponse refresh(RefreshRequest request) {
    String refreshToken = request.refreshToken();
    if (!jwtTokenProvider.isValid(refreshToken)) {
      throw new InvalidRefreshTokenException();
    }
    String email = jwtTokenProvider.getEmail(refreshToken);
    if (!refreshTokenStore.matches(email, refreshToken)) {
      throw new InvalidRefreshTokenException();
    }
    return issueTokens(email);
  }

  @Transactional
  public void logout(String email) {
    refreshTokenStore.delete(email);
  }

  public UserResponse me(String email) {
    User user =
        userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    return UserResponse.from(user);
  }

  private TokenResponse issueTokens(String email) {
    String accessToken = jwtTokenProvider.generateAccessToken(email);
    String refreshToken = jwtTokenProvider.generateRefreshToken(email);
    refreshTokenStore.save(email, refreshToken);
    return new TokenResponse(accessToken, refreshToken);
  }
}
