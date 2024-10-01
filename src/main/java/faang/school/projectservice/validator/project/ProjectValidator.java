package faang.school.projectservice.validator.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ProjectValidator {

    private final ProjectRepository projectRepository;

    public void validateProject(ProjectDto projectDto) {
        validateName(projectDto);
        validateDescription(projectDto);
        validateOwnerHasSameProject(projectDto);
    }

    public void validateUpdatedFields(ProjectDto projectDto) {
        if ((projectDto.getDescription() == null || projectDto.getDescription().isBlank())
                && projectDto.getStatus() == null) {
            throw new DataValidationException("At least one updated field must not be empty");
        }
    }

    private void validateOwnerHasSameProject(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException("Owner already has a project with name " + projectDto.getName());
        }
    }

    private void validateDescription(ProjectDto projectDto) {
        if (projectDto.getDescription() == null || projectDto.getDescription().isBlank()) {
            throw new DataValidationException("Field description cannot be empty or null");
        }
    }

    private void validateName(ProjectDto projectDto) {
        if (projectDto.getName() == null || projectDto.getName().isBlank()) {
            throw new DataValidationException("Field name cannot be empty or null");
        }
    }
}