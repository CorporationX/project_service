package faang.school.projectservice.service.project.validation;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.ValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectServiceValidator {

    private final ProjectRepository projectRepository;

    public void validateCreation(Long userId, ProjectDto projectDto) {
        if (projectRepository.findAll().stream()
                .filter(project -> project.getOwnerId().equals(userId))
                .anyMatch(project -> project.getName().equals(projectDto.getName()))) {
            log.info("Project with name {} already exists.", projectDto.getName());
            throw new ValidationException("Project with name " + projectDto.getName() + " already exists.");
        }
    }

    public void validateUpdating(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            log.info("Project with id {} does not exist.", projectId);
            throw new EntityNotFoundException("Project with id " + projectId + " does not exist.");
        }
    }
}
