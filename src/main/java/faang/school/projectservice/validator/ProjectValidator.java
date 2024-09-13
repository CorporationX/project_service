package faang.school.projectservice.validator;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;

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
