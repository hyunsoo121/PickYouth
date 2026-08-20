package com.Hyunsoo.PickYouth.domain.auth.dto;

import com.Hyunsoo.PickYouth.domain.user.entity.User;

public record UserResponse(Long id, String email, String name) {

  public static UserResponse from(User user) {
    return new UserResponse(user.getId(), user.getEmail(), user.getName());
  }
}
