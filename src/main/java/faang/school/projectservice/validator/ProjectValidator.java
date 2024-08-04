package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.ValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectValidator {

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
            Project project = projectRepository.findById(projectDto.getId()).orElseThrow(() -> {
                log.info("Project with id {} does not exist.", projectDto.getId());
                return new ValidationException("Project with id " + projectDto.getId() + " does not exist.");
            });
            if (project.getStatus() == ProjectStatus.COMPLETED || project.getStatus() == ProjectStatus.CANCELLED) {
                log.info("Project with id {} completed or cancelled", projectDto.getId());
                throw new ValidationException("Project with id " + projectDto.getId() + " completed or cancelled.");
            }
        } else {
            log.info("Field id is null");
            throw new ValidationException("Field id is null");
        }
    }

    public void validateProjectFilterDtoForFindById(ProjectFilterDto projectFilterDto) {
        if (projectFilterDto.getIdPattern() == null) {
            log.info("Field id pattern is null");
            throw new ValidationException("Field id pattern is null");
        }
    }
}
