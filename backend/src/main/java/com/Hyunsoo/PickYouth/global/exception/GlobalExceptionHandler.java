package com.Hyunsoo.PickYouth.global.exception;

import com.Hyunsoo.PickYouth.domain.auth.exception.DuplicateEmailException;
import com.Hyunsoo.PickYouth.domain.auth.exception.InvalidCredentialsException;
import com.Hyunsoo.PickYouth.domain.auth.exception.InvalidRefreshTokenException;
import com.Hyunsoo.PickYouth.domain.subsidy.exception.InvalidPageSizeException;
import com.Hyunsoo.PickYouth.domain.subsidy.exception.SubsidyNotFoundException;
import com.Hyunsoo.PickYouth.domain.user.exception.UserNotFoundException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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
            .map(this::describeFieldError)
            .collect(Collectors.joining(", "));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(message));
  }

  // 값이 있는데 타입 변환에 실패한 경우(예: age=abc)는 code가 "typeMismatch"로 잡히고
  // getDefaultMessage()가 Spring의 영문 원본 메시지를 그대로 반환하므로 직접 메시지를 만든다.
  private String describeFieldError(FieldError fieldError) {
    String code = fieldError.getCode();
    if (code != null && code.contains("typeMismatch")) {
      return fieldError.getField() + " 값이 올바르지 않습니다: " + fieldError.getRejectedValue();
    }
    return fieldError.getDefaultMessage();
  }

  @ExceptionHandler(InvalidPageSizeException.class)
  public ResponseEntity<ErrorResponse> handleInvalidPageSize(InvalidPageSizeException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
    String message = e.getName() + " 값이 올바르지 않습니다: " + e.getValue();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(message));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
    log.error("Unhandled exception", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse("서버 오류가 발생했습니다."));
  }
}
