package faang.school.projectservice.validations.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.validations.annotations.ShouldBePublic;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
    @Component
    @RequiredArgsConstructor
    public class ShouldBePublicValidator implements ConstraintValidator<ShouldBePublic, Project> {

        public void shouldBePublic(Project project) {
            if (project.getVisibility().equals(ProjectVisibility.PRIVATE)) {
                throw new DataValidationException("Parent project is private");
            }
        }

        @Override
        public boolean isValid(Project value, ConstraintValidatorContext context) {
            shouldBePublic(value);
            return true;
        }
    }

