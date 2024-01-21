package faang.school.projectservice.validation;

import faang.school.projectservice.dto.project.ProjectDto;

public class ProjectValidate {
    public static void validateProjectDtoToCreate (ProjectDto projectDto) {
        if (projectDto.getName().isEmpty() || projectDto.getName().isBlank()) {
            throw new IllegalArgumentException("Name of project cannot be empty or bland");
        }
    }
}
