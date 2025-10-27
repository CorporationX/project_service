package faang.school.projectservice.service.project.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;

import java.util.Objects;

public class ProjectValidator {
    public static void validateProjectOwner(long userId, Project project) {
        if (!Objects.equals(userId, project.getOwnerId())) {
            throw new DataValidationException("You can only update your own projects");
        }
    }

    public static void validateBlankFields(String name, String description) {
        if (name.isBlank()) {
            throw new DataValidationException("Project name cannot be empty");
        }
        if (description.isBlank()) {
            throw new DataValidationException("Project description cannot be empty");
        }
    }
}
