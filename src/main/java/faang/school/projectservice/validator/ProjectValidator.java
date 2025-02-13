package faang.school.projectservice.validator;

import faang.school.projectservice.model.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidator {

    public void validateUserId(long userId) {

        if (userId <= 0) {
            throw new IllegalArgumentException("User id must be more 0!");
        }
    }

    public void validateUserIsOwner(long userId, Project project) {

        if (userId != project.getOwnerId()) {
            throw new IllegalArgumentException("Only the project owner can request a presentation!");
        }
    }
}
