package com.Hyunsoo.PickYouth.global.exception;

import com.Hyunsoo.PickYouth.domain.subsidy.exception.SubsidyNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(SubsidyNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleSubsidyNotFound(SubsidyNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
  }
}
