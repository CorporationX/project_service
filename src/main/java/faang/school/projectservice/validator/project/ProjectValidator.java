package faang.school.projectservice.validator.project;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.model.entity.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidator {
    private final ProjectRepository projectRepository;

    public Project validateProject(long projectId){
        Project project = projectRepository.getProjectById(projectId);

        if(project.getStatus() == ProjectStatus.CANCELLED){
            throw new DataValidationException("Project %s is cancelled".formatted(projectId));
        }

        return project;
    }
}
