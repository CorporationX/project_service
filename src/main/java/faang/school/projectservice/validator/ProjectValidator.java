package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.excepcion.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidator {
    public void validate(ProjectDto projectDto) {
        if (projectDto.getName() == null || projectDto.getName().isBlank()) {
           throw new DataValidationException("Project name cannot be empty");
        }
        if (projectDto.getDescription() == null || projectDto.getDescription().isBlank()) {
            throw new DataValidationException("Project description cannot be empty");
        }
    }
}
