package faang.school.projectservice.validator.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.repository.ProjectRepository;
import org.springframework.stereotype.Component;

@Component
public class ProjectValidator {
    ProjectRepository projectRepository;

    public boolean id(ProjectDto projectDto) {
        if (projectDto.getId() == null || projectDto.getId().equals(0L)) {
            throw new RuntimeException("Invalid id " + projectDto.getId());
        }
        return true;
    }

    public boolean id(Long id) {
        if (id == null || id.equals(0L)) {
            throw new RuntimeException("Invalid id " + id);
        }
        return true;
    }

    public boolean name(ProjectDto projectDto) {
        if (projectDto.getName() == null || projectDto.getName().isBlank()) {
            throw new RuntimeException("Invalid name " + projectDto.getName());
        }
        return true;
    }

    public boolean description(ProjectDto projectDto) {
        if (projectDto.getDescription() == null || projectDto.getDescription().isBlank()) {
            throw new RuntimeException("Invalid description " + projectDto.getDescription());
        }
        return true;
    }

    public boolean existsByOwnerUserIdAndName(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new RuntimeException("Project " + projectDto.getName() + " already created by " + projectDto.getOwnerId());
        }
        return true;
    }
}
