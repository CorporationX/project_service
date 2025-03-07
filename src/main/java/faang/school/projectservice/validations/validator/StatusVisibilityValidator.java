package faang.school.projectservice.validations.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.validations.annotations.StatusVisibility;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatusVisibilityValidator implements ConstraintValidator<StatusVisibility, Project> {
    public void validateStatusVisibilityNotNull(ProjectStatus status, ProjectVisibility visibility) {
        if (status == null && visibility == null) {
            throw new DataValidationException("Updating parameters required");
        }
    }

    @Override
    public boolean isValid(Project value, ConstraintValidatorContext context) {
       validateStatusVisibilityNotNull(value.getStatus(), value.getVisibility());
        return true;
    }
}
