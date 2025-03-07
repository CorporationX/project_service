package faang.school.projectservice.validations.annotations;

import faang.school.projectservice.validations.validator.StatusProjectValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = StatusProjectValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProjectStatus {
    String message() default "Invalid project status";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
