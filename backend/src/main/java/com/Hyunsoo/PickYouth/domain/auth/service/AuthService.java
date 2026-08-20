package com.Hyunsoo.PickYouth.domain.auth.service;

import com.Hyunsoo.PickYouth.domain.auth.dto.LoginRequest;
import com.Hyunsoo.PickYouth.domain.auth.dto.RefreshRequest;
import com.Hyunsoo.PickYouth.domain.auth.dto.SignupRequest;
import com.Hyunsoo.PickYouth.domain.auth.dto.TokenResponse;
import com.Hyunsoo.PickYouth.domain.auth.dto.UserResponse;
import com.Hyunsoo.PickYouth.domain.auth.exception.DuplicateEmailException;
import com.Hyunsoo.PickYouth.domain.auth.exception.InvalidCredentialsException;
import com.Hyunsoo.PickYouth.domain.auth.exception.InvalidRefreshTokenException;
import com.Hyunsoo.PickYouth.domain.user.entity.User;
import com.Hyunsoo.PickYouth.domain.user.exception.UserNotFoundException;
import com.Hyunsoo.PickYouth.domain.user.repository.UserRepository;
import com.Hyunsoo.PickYouth.global.security.JwtTokenProvider;
import com.Hyunsoo.PickYouth.global.security.RefreshTokenStore;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenStore refreshTokenStore;

  public AuthService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager,
      JwtTokenProvider jwtTokenProvider,
      RefreshTokenStore refreshTokenStore) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtTokenProvider = jwtTokenProvider;
    this.refreshTokenStore = refreshTokenStore;
  }

  @Transactional
  public UserResponse signup(SignupRequest request) {
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

  public TokenResponse login(LoginRequest request) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.email(), request.password()));
    } catch (AuthenticationException e) {
      throw new InvalidCredentialsException();
    }
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
