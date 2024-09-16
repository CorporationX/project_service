package faang.school.projectservice.validation;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import org.springframework.stereotype.Component;

@Component
public class ProjectDtoValidator {

    public void validateDtoCreate(ProjectDto projectDto) {
        validateId(projectDto.getOwnerId());
        String projectName = projectDto.getName();
        if (projectName == null) {
            throw new DataValidationException("Project name is required");
        }
        if (projectName.isBlank()) {
            throw new DataValidationException("Project name cannot be blank");
        }
    }

    public void validateDtoUpdate(ProjectDto projectDto) {
        if (projectDto.getDescription() == null && projectDto.getStatus() == null) {
            throw new DataValidationException("Project description or project status is required");
        }
    }

    public void validateId(Long id) {
        if (id < 0) {
            throw new DataValidationException("ID has incorrect value: %d"
                    .formatted(id));
        }
    }
}
