package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    public Project getProjectById(Long projectId) {
        Project project = projectRepository.getProjectById(projectId);

        if (project == null) {
           throw new IllegalArgumentException("Project's id not found");
        }

        return project;
    }

    public void save(Project project) {
        projectRepository.save(project);
    }

}
