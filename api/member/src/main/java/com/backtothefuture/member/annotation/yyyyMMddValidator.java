package com.backtothefuture.member.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class yyyyMMddValidator implements ConstraintValidator<ValidyyyyMMdd, String> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            // 값이 null 이거나 빈 문자열일 경우
            context.disableDefaultConstraintViolation(); // 기본 메시지 비활성화
            context.buildConstraintViolationWithTemplate("입력값이 빈 문자열이거나 null 일수 없습니다.")
                    .addConstraintViolation();
            return false;
        }

        try {
            LocalDate.parse(value, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            // 포맷이 맞지 않을 경우, 기본 메시지
            return false;
        }

        // 유효성 검사를 통과했으면 true 반환
        return true;
    }
}