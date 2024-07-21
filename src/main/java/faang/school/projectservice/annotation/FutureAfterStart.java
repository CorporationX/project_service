package faang.school.projectservice.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FutureAfterStartValidation.class)
public @interface FutureAfterStart {
    String message() default "endDate cannot be before startDate";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
