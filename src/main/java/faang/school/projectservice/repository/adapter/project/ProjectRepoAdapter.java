package faang.school.projectservice.repository.adapter.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectRepoAdapter {
    private final ProjectRepository projectRepository;

    public Project getProjectById(long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }
}
