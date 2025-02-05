package faang.school.projectservice.service.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    public Project getProject(long projectId) {
        return projectRepository.findById(projectId).orElseThrow(
            () -> new EntityNotFoundException(
                String.format("Project not found by id: %s", projectId))
        );
    }

    public void saveProject(Project project){
        projectRepository.save(project);
    }
}
