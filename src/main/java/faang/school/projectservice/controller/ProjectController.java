package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/project")
    public ProjectDto createProject(@RequestBody ProjectDto projectDto) {
        validate(projectDto);
        return projectService.createProject(projectDto);
    }

    private void validate(ProjectDto projectDto) {
        if(projectDto == null) {
            throw new DataValidationException("ProjectDto cannot be null");
        }
        if(projectDto.getName() == null || projectDto.getName().isBlank()) {
            throw new DataValidationException("ProjectDto name cannot be null or empty");
        }
        if(projectDto.getDescription() == null || projectDto.getDescription().isBlank()) {
            throw new DataValidationException("ProjectDto description cannot be null or empty");
        }
    }
}
