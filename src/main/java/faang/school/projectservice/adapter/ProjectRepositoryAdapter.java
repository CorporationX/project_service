package faang.school.projectservice.adapter;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class ProjectRepositoryAdapter {

    private ProjectRepository projectRepository;

    public Project getProjectById ( long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project with id " + projectId + " not found"));
    }

}
