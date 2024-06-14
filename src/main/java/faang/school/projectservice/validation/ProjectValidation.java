package faang.school.projectservice.validation;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class ProjectValidation {
    private final ProjectRepository projectRepository;

    public void checkProjectExists(long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new DataValidationException("Project with projectId=" + projectId + " not exist");
        }
    }

    public void checkProjectStatuses(long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        if (ProjectStatus.COMPLETED.equals(project.getStatus())) {
            throw new DataValidationException("It is impossible to create a moment " +
                    "because the project is in status completed");
        }
    }
}
