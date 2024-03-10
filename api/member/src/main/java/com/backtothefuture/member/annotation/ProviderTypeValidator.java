package com.backtothefuture.member.annotation;

import com.backtothefuture.domain.member.enums.ProviderType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class ProviderTypeValidator implements
    ConstraintValidator<EnumTypeMisMatch, ProviderType> {

    @Override
    public void initialize(EnumTypeMisMatch constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(ProviderType value, ConstraintValidatorContext context) {
        /**
         * provierType이 null일때와 KAKAO2처럼 일치하는 값이 없을 때, 서로 다른 message를 출력하는 방법이 있을까요?
         */
//        FieldError fieldError = new FieldError("providerType enum", "providerType",
//            "providerType은 필수입니다.");
//        BeanPropertyBindingResult providerTypeEnum = new BeanPropertyBindingResult(null,
//            "providerType enum");
//        providerTypeEnum.addError(fieldError);
//        if (value == null) {
//            throw new MethodArgumentNotValidException(null,providerTypeEnum);
//        }
        return Arrays.stream(ProviderType.values()).anyMatch(providerType -> providerType == value);

    }
}
