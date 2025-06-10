package faang.school.projectservice.repository.adapter.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProjectRepositoryAdapter {
    private final ProjectRepository projectRepository;

    public Project getProjectById(long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }

    public List<Project> getAllProjectsById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Ids are empty");
        }

        List<Project> loaded = projectRepository.findAllById(ids);

        Map<Long, Project> map = loaded.stream()
                .collect(Collectors.toMap(Project::getId, Function.identity()));

        List<Project> result = new ArrayList<>(ids.size());
        List<Long> missing = new ArrayList<>();

        for (Long id : ids) {
            Project p = map.get(id);
            if (p == null) {
                missing.add(id);
            } else {
                if (p.getStatus() == ProjectStatus.CANCELLED) {
                    throw new IllegalArgumentException(
                            "Cannot create moment for cancelled project: " + p.getName());
                }
                result.add(p);
            }
        }

        if (!missing.isEmpty()) {
            throw new EntityNotFoundException("Projects not found for IDs: " + missing);
        }

        return result;
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
