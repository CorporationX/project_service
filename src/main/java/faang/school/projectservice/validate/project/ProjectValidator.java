package faang.school.projectservice.validate.project;

import faang.school.projectservice.exception.ProjectStatusException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidator {
    private final ProjectRepository projectRepository;

    public void validateProjectNotCancelled(long projectId) {
        Project project = projectRepository.getProjectById(projectId);

        if (project.getStatus() == ProjectStatus.CANCELLED) {
            throw new ProjectStatusException("Project with id " + project.getId() + " is cancelled");
        }
    }

    public void validateProjectNotCompleted(long projectId) {
        Project project = projectRepository.getProjectById(projectId);

        if (project.getStatus() == ProjectStatus.COMPLETED) {
            throw new ProjectStatusException("Project with id " + project.getId() + " is completed");
        }
    }
}
