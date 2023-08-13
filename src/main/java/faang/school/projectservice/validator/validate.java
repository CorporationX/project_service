package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;

public class validate {
    private void validateProject(ProjectDto projectDto) {
        if (projectDto == null) {
            throw new DataValidationException("Project is null");
        }
        if (projectDto.getName().length() < 128) {
            throw new DataValidationException("Project name should be less than 128 character");
        }
        if (projectDto.getId() == null) {
            throw new DataValidationException("Project id is null");
        }
        if (projectDto.getDescription().length() < 4096) {
            throw new DataValidationException("Project description should be less than 4096 character");
        }
        if (projectDto.getDescription().isEmpty()) {
            throw new DataValidationException("Project description is empty");
        }
        if (projectDto.getStatus() == null) {
            throw new DataValidationException("Project status is empty");
        }
        if (projectDto.getName().isBlank()) {
            throw new DataValidationException("Project name is empty");
        }
    }
}
