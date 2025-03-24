package faang.school.projectservice.validations.annotations;

import faang.school.projectservice.validations.validator.ShouldBePublicValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
    @Constraint(validatedBy = ShouldBePublicValidator.class)
    @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ShouldBePublic {
        String message() default "Project not found";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

