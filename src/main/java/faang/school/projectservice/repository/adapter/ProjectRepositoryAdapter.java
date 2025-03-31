package faang.school.projectservice.repository.adapter;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectRepositoryAdapter {
    private final ProjectRepository projectRepository;

    public Project getById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project with ID " + id + " not found"));
    }
}
