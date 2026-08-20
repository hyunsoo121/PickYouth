package com.Hyunsoo.PickYouth.global.exception;

import com.Hyunsoo.PickYouth.domain.auth.exception.DuplicateEmailException;
import com.Hyunsoo.PickYouth.domain.auth.exception.InvalidCredentialsException;
import com.Hyunsoo.PickYouth.domain.auth.exception.InvalidRefreshTokenException;
import com.Hyunsoo.PickYouth.domain.subsidy.exception.SubsidyNotFoundException;
import com.Hyunsoo.PickYouth.domain.user.exception.UserNotFoundException;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(SubsidyNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleSubsidyNotFound(SubsidyNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
  }

  @ExceptionHandler(DuplicateEmailException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
  }

  @ExceptionHandler({InvalidCredentialsException.class, InvalidRefreshTokenException.class})
  public ResponseEntity<ErrorResponse> handleUnauthorized(RuntimeException e) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getMessage()));
  }

  @ExceptionHandler(BindException.class)
  public ResponseEntity<ErrorResponse> handleBindException(BindException e) {
    String message =
        e.getFieldErrors().stream()
            .map(fieldError -> fieldError.getDefaultMessage())
            .collect(Collectors.joining(", "));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(message));
  }
}
