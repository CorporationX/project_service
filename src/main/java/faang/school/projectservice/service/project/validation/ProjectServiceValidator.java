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

    public void validateUpdating(ProjectDto projectDto) {
        if (projectDto.getId() != null) {
            if (!projectRepository.existsById(projectDto.getId())) {
                log.info("Project with id {} does not exist.", projectDto.getId());
                throw new EntityNotFoundException("Project with id " + projectDto.getId() + " does not exist.");
            }
        } else {
            log.info("Field id is null");
            throw new ValidationException("Field id is null");
        }
    }
}
