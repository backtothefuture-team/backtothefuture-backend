package com.backtothefuture.store.annotation;

import java.util.List;
import java.util.Optional;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NumericStringListValidator implements ConstraintValidator<NumericStringList, List<String>> {
	@Override
	public void initialize(NumericStringList constraintAnnotation) {
	}

	@Override
	public boolean isValid(List<String> values, ConstraintValidatorContext context) {
		return Optional.ofNullable(values)
			.map(list -> list.stream().allMatch(value -> value != null && value.matches("\\d+")))
			.orElse(true);
	}
}
