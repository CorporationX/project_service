package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;
    private static final int MAX_NAME_LENGTH = 128;
    private static final int MAX_DESCRIPTION_LENGTH = 4096;

    @PostMapping
    public ProjectDto create(@RequestBody ProjectDto projectDto) {
        validateCreateProject(projectDto);
        return projectService.create(projectDto);
    }

    @PutMapping("/update")
    public ProjectDto update(@RequestBody ProjectDto projectDto) {
        validateUpdateProject(projectDto);
        return projectService.update(projectDto);
    }

    @PostMapping("/{userId}")
    public List<ProjectDto> getProjectWithFilters(@RequestBody ProjectFilterDto projectFilterDto, @PathVariable long userId) {
        if (userId <= 0) {
            throw new DataValidationException("UserId can't be negative or zero");
        }
        return projectService.getProjectsWithFilter(projectFilterDto, userId);
    }

    private void validateCreateProject(ProjectDto projectDto) {
        String name = projectDto.getName();
        String description = projectDto.getDescription();
        if (name == null || name.isBlank()) {
            throw new DataValidationException("Project can't be created with empty name");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new DataValidationException("Project's name length can't be more than 128 symbols");
        }
        if (description == null || description.isBlank()) {
            throw new DataValidationException("Project can't be created with empty description");
        }
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new DataValidationException("Project's description length can't be more than 4096 symbols");
        }
    }

    private void validateUpdateProject(ProjectDto projectDto) {
        if (projectDto.getId() <= 0) {
            throw new DataValidationException("Id can't be negative or zero");
        }
        if (projectDto.getDescription() != null) {
            if (projectDto.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
                throw new DataValidationException("Project's description length can't be more than 4096 symbols");
            }
        }
    }
}
