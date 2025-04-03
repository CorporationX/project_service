package faang.school.projectservice.service.project.implementations;

import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.interfaces.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;

    @Override
    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.PROJECT_NOT_FOUND.getMessage(projectId)));
    }

    @Override
    public void save(Project project) {
        projectRepository.save(project);
    }
}
