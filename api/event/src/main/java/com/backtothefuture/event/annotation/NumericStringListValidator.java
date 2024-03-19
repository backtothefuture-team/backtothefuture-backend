package com.backtothefuture.event.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class NumericStringListValidator implements ConstraintValidator<NumericStringList, List<String>> {
    @Override
    public void initialize(NumericStringList constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<String> values, ConstraintValidatorContext context) {
        return values.stream().allMatch(value -> value.matches("\\d+"));
    }
}
