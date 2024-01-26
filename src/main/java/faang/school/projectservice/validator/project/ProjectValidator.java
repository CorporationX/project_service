package faang.school.projectservice.validator.project;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProjectValidator {

    public static void validateName (String name, long ownerId) {
        if (name.isEmpty() || name.isBlank()) {
            throw new IllegalArgumentException("Name of project cannot be empty or blank");
        }
    }

    public static void validateDescription(String description) {
        if (description != null && (description.isEmpty() || description.isBlank())) {
            throw new IllegalArgumentException("Description of project cannot be empty or blank");
        }
    }

    public static void validateAccessToProject(long ownerId, long authUserId) {
        if (!haveAccessToProject(ownerId, authUserId)) {
            throw new SecurityException("User is not the owner of the project");
        }
    }

    public static boolean haveAccessToProject(long ownerId, long authUserId) {
        return authUserId == ownerId;
    }
}
