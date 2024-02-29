package com.backtothefuture.member.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Documented - JavaDoc 문서에 포함되어야 함을 나타낸다.
 * Constraint - 검증 클래스 설정(제약조건)
 * Target - 어노테이션이 적용될 수 있는 범위(대상)을 나타낸다.
 * Retention - 유지정책을 표시함.
 */
@Documented
@Constraint(validatedBy = NumericStringListValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NumericStringList {
	String message() default "숫자만 입력할 수 있습니다.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
