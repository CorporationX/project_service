package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SubProjectController {
    private final ProjectService projectService;

    public ProjectDto createSubProject(ProjectDto projectDto) {
        validateSubProject(projectDto);
        return projectService.createSubProject(projectDto);
    }

    private void validateSubProject(ProjectDto projectDto) {
        if (projectDto.getName().isBlank()) {
            throw new DataValidationException("Enter project name, please");
        }
    }
}
