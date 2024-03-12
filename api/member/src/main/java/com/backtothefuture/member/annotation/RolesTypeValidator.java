package com.backtothefuture.member.annotation;

import com.backtothefuture.domain.member.enums.RolesType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class RolesTypeValidator implements ConstraintValidator<EnumTypeMisMatch, RolesType> {

    @Override
    public void initialize(EnumTypeMisMatch constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(RolesType value, ConstraintValidatorContext context) {
        return Arrays.stream(RolesType.values()).anyMatch(rolesType -> rolesType == value);
    }
}
