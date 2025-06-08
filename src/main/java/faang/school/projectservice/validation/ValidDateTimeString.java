package faang.school.projectservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidDateTimeStringValidator.class)
public @interface ValidDateTimeString {
    String message() default "Invalid date-time format. Expected format (e.g., yyyy-MM-dd'T'HH:mm:ssXXX or yyyy-MM-dd'T'HH:mm:ssZ).";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}