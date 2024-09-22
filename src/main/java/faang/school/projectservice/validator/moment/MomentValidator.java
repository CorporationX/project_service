package faang.school.projectservice.validator.moment;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MomentValidator {
    public void validateNoCancelledProjects(List<Project> projects) {
        projects.stream()
                .filter(project -> project.getStatus().equals(ProjectStatus.CANCELLED))
                .findFirst()
                .ifPresent(project -> {
                    throw new DataValidationException("Found cancelled project with id " + project.getId());
                });
    }
}
