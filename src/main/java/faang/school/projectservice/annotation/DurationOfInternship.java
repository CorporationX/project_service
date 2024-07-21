package faang.school.projectservice.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DurationOfInternshipValidation.class)
public @interface DurationOfInternship {
    String message() default "the duration of the internship should be no more than 3 months";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
