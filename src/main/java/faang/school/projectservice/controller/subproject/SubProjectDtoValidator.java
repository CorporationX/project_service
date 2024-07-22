package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import org.springframework.stereotype.Component;

@Component
public class SubProjectDtoValidator {
    public void validate(CreateSubProjectDto subProjectDto) {
        if (subProjectDto.getParentProjectId() == null) {
            throw new DataValidationException("Parent Project ID can't be null");
        } else if (subProjectDto.getId() != null) {
            throw new DataValidationException("SubProject ID should be null for creating");
        } else if (subProjectDto.getName() == null || subProjectDto.getName().isBlank()) {
            throw new DataValidationException("Project name can't be null or empty");
        } else if (subProjectDto.getDescription() == null || subProjectDto.getDescription().isBlank()) {
            throw new DataValidationException("Project description can't be null or empty");
        }
    }
}
