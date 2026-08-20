package com.Hyunsoo.PickYouth.domain.auth.controller;

import com.Hyunsoo.PickYouth.domain.auth.dto.LoginRequest;
import com.Hyunsoo.PickYouth.domain.auth.dto.RefreshRequest;
import com.Hyunsoo.PickYouth.domain.auth.dto.SignupRequest;
import com.Hyunsoo.PickYouth.domain.auth.dto.TokenResponse;
import com.Hyunsoo.PickYouth.domain.auth.dto.UserResponse;
import com.Hyunsoo.PickYouth.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "회원가입/로그인/토큰 재발급")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @Operation(summary = "회원가입")
  @PostMapping("/signup")
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponse signup(@Valid @RequestBody SignupRequest request) {
    return authService.signup(request);
  }

  @Operation(summary = "로그인", description = "성공 시 access/refresh token을 함께 발급한다.")
  @PostMapping("/login")
  public TokenResponse login(@Valid @RequestBody LoginRequest request) {
    return authService.login(request);
  }

  @Operation(
      summary = "토큰 재발급",
      description = "refresh token 검증 후 access/refresh token을 모두 새로 발급한다(회전).")
  @PostMapping("/refresh")
  public TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
    return authService.refresh(request);
  }

  @Operation(summary = "내 정보 조회")
  @GetMapping("/me")
  public UserResponse me(@AuthenticationPrincipal UserDetails userDetails) {
    return authService.me(userDetails.getUsername());
  }
}
