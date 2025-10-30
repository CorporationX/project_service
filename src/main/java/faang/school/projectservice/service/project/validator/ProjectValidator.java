package faang.school.projectservice.service.project.validator;

import faang.school.projectservice.model.Project;

import java.util.Objects;

public class ProjectValidator {
    public static void validateProjectOwner(long userId, Project project) {
        if (!Objects.equals(userId, project.getOwnerId())) {
            throw new IllegalArgumentException("You can only update your own projects");
        }
    }
}
