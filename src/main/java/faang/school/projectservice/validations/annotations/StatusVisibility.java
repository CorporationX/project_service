package faang.school.projectservice.validations.annotations;

import faang.school.projectservice.validations.validator.StatusVisibilityValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = StatusVisibilityValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StatusVisibility {
    String message() default "Invalid data";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
