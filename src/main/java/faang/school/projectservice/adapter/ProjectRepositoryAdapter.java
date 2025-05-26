package faang.school.projectservice.adapter;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class ProjectRepositoryAdapter {
    public Project projectFromRepository (ProjectRepository repository, long projectId) {
        return repository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project with id " + projectId + " not found"));
    }

}
