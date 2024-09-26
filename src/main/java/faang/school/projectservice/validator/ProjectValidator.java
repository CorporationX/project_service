package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidator {
    public void validateProject(Project project) {
        if (project.getStatus().equals(ProjectStatus.CANCELLED)) {
            throw new DataValidationException("Project has status CANCELLED");
        }
        if (project.getStatus().equals(ProjectStatus.COMPLETED)) {
            throw new DataValidationException("Project has status COMPLETED");
        }
    }
}
