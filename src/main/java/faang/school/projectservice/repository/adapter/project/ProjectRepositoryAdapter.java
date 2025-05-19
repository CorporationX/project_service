package faang.school.projectservice.repository.adapter.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectRepositoryAdapter {
    private final ProjectRepository projectRepository;

    public List<Project> getAllProjectsById(List<Long> ids) {
        List<Project> projects = projectRepository.findAllById(ids);

        for (Project project : projects) {
            if (project.getStatus() == ProjectStatus.CANCELLED) {
                throw new IllegalArgumentException("Cannot create moment for" +
                        " cancelled project: " + project.getName());
            }
        }
        return projects;
    }

    public Project getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Project not found with id: " + id));

        if (project.getStatus() == ProjectStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot create moment for" +
                    " cancelled project: " + project.getName());
        }
        return project;
    }
}
