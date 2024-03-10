package com.backtothefuture.member.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ProviderTypeValidator.class, RolesTypeValidator.class})
public @interface EnumTypeMisMatch {

    String message() default "enum mismatch error 발생!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
