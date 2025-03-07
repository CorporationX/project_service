package faang.school.projectservice.validations.annotations;
import faang.school.projectservice.validations.validator.ChildCompletedValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Constraint(validatedBy = ChildCompletedValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
    public @interface ChildCompleted {
        String message() default "There is no such project";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

