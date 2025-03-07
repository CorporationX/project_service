package faang.school.projectservice.validations.validator;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.validations.annotations.ProjectStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatusProjectValidator implements ConstraintValidator<ProjectStatus, Project> {
    public boolean isPublicProject(Project project) {
        return project.getVisibility().equals(ProjectVisibility.PUBLIC);
    }

    @Override
    public boolean isValid(Project value, ConstraintValidatorContext context) {
        isPublicProject(value);
        return true;
    }
}
