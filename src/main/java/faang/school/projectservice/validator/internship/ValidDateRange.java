package faang.school.projectservice.validator.internship;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.TYPE;

@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
public @interface ValidDateRange {
    String message() default "Error: Wrong combination dates.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}