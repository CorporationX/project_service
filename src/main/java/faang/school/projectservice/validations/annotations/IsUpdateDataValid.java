package faang.school.projectservice.validations.annotations;

import faang.school.projectservice.validations.validator.IsCreateDataValidValidator;
import faang.school.projectservice.validations.validator.IsUpdateDataValidValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = IsUpdateDataValidValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsUpdateDataValid {
    String message() default "Invalid Data";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
