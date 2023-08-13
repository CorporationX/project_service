package faang.school.projectservice.service.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    public Project getProjectById(long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    public boolean isExistProjectById(long projectId) {
        return projectRepository.existsById(projectId);
    }
}
