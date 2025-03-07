package faang.school.projectservice.validations.validator;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.validations.annotations.ChildCompleted;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import faang.school.projectservice.exception.DataValidationException;

import java.util.List;
import java.util.Objects;

public class ChildCompletedValidator implements ConstraintValidator<ChildCompleted, Project> {
    public void childCompleted(List<Project> projects) {
        if (projects != null) {
            for (Project project : projects) {
                if (!(Objects.equals(project.getStatus(), ProjectStatus.COMPLETED)) &&
                        !(Objects.equals(project.getStatus(), ProjectStatus.CANCELLED))) {
                    throw new DataValidationException("Project can't be closed");
                }
            }
        }
    }
    @Override
    public boolean isValid(Project value, ConstraintValidatorContext context) {
        childCompleted(value != null ? List.of(value) : List.of());
        return true;
    }
}
