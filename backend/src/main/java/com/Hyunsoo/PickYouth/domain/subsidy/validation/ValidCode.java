package com.Hyunsoo.PickYouth.domain.subsidy.validation;

import com.Hyunsoo.PickYouth.domain.subsidy.code.CodeEnum;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** null/blank는 통과시키고(선택값이므로), 값이 있으면 {@link #value()} enum의 코드 목록에 포함되는지 검증한다. */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCodeValidator.class)
public @interface ValidCode {

  Class<? extends Enum<? extends CodeEnum>> value();

  String message() default "유효하지 않은 코드입니다.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
