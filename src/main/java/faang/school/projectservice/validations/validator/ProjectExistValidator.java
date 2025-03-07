package faang.school.projectservice.validations.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.validations.annotations.ProjectExist;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProjectExistValidator implements ConstraintValidator<ProjectExist, Project> {

    public void doesProjectExist(Optional<Project> project) {
        if (project.isEmpty()) {
            throw new DataValidationException("Project not found");
        }
    }

    @Override
    public boolean isValid(Project value, ConstraintValidatorContext context) {
        doesProjectExist(Optional.ofNullable(value));
        return true;
    }
}
