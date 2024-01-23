package faang.school.projectservice.validator.project;

import faang.school.projectservice.dto.project.ProjectDto;

public class ProjectValidator {
    public static void validateProjectName(ProjectDto projectDto) {
        if (projectDto.getName().isEmpty() || projectDto.getName().isBlank()) {
            throw new IllegalArgumentException("Name of project cannot be empty or blank");
        }
    }
}