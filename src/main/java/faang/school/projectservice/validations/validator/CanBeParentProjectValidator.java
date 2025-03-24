package faang.school.projectservice.validations.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.validations.annotations.ProjectExist;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CanBeParentProjectValidator implements ConstraintValidator<ProjectExist, Project> {

    public void canBeParentProject(Project parentProject) {
        boolean canBe = parentProject.getParentProject() == null &&
                parentProject.getStatus() != ProjectStatus.COMPLETED &&
                parentProject.getStatus() != ProjectStatus.CANCELLED;
        if (!canBe) {
            throw new DataValidationException("Project can't be parent");
        }
    }

        @Override
        public boolean isValid(Project value, ConstraintValidatorContext context) {
            canBeParentProject(value);
            return true;
        }
    }

