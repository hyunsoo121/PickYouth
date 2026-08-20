package com.Hyunsoo.PickYouth.domain.subsidy.validation;

import com.Hyunsoo.PickYouth.domain.subsidy.code.CodeEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidCodeValidator implements ConstraintValidator<ValidCode, String> {

  private Set<String> validCodes;

  @Override
  public void initialize(ValidCode annotation) {
    validCodes =
        Arrays.stream(annotation.value().getEnumConstants())
            .map(constant -> ((CodeEnum) constant).code())
            .collect(Collectors.toSet());
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return value == null || value.isBlank() || validCodes.contains(value);
  }
}
