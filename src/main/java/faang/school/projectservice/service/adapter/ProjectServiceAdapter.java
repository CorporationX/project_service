package faang.school.projectservice.service.adapter;

import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceAdapter implements ProjectService {
    private final ProjectRepository projectRepository;

    @Override
    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException(projectId));
    }
}
