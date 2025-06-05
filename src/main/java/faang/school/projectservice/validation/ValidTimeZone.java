package faang.school.projectservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidTimeZoneValidator.class)
public @interface ValidTimeZone {
    String message() default "Invalid IANA time zone ID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}