package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ProjectController {

    private final ProjectService projectService;
    private static final int MAX_NAME_LENGTH = 128;
    private static final int MAX_DESCRIPTION_LENGTH = 4096;

    @PostMapping("/project")
    public ProjectDto create(@RequestBody ProjectDto projectDto) {
        validateCreateProject(projectDto);
        return projectService.create(projectDto);
    }
    @PostMapping("/projectUpdate/{projectId}")
    public ProjectDto update(@RequestBody ProjectDto projectDto,@PathVariable long projectId) {
        validateUpdateProject(projectDto,projectId);
        return projectService.update(projectDto, projectId);
    }

    @PostMapping("/project/{userId}")
    public List<ProjectDto> getProjectWithFilters(@RequestBody ProjectFilterDto projectFilterDto,@PathVariable long userId){
        return projectService.getProjectsWithFilter(projectFilterDto, userId);
    }

    @GetMapping("/project/{userId}")
    public List<ProjectDto> getAllProjects(@PathVariable long userId) {
        return projectService.getAllProjects(userId);
    }

    public ProjectDto getProjectById(long projectId, List<Team> userTeams) {
        if (projectId <= 0) {
            throw new DataValidationException("Id can't be negative or zero");
        }
        return projectService.getProjectById(projectId, userTeams);
    }

    private void validateCreateProject(ProjectDto projectDto) {
        if (projectDto.getName() == null || projectDto.getName().isBlank()) {
            throw new DataValidationException("Project can't be created with empty name");
        }
        if (projectDto.getName().length() > MAX_NAME_LENGTH){
            throw new DataValidationException("Project's name length can't be more than 128 symbols");
        }
        if (projectDto.getDescription() == null || projectDto.getDescription().isBlank()) {
            throw new DataValidationException("Project can't be created with empty description");
        }
        if (projectDto.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            throw new DataValidationException("Project's description length can't be more than 4096 symbols");
        }
    }

    private void validateUpdateProject(ProjectDto projectDto, long projectId) {
        if (projectId <= 0) {
            throw new DataValidationException("Id can't be negative or zero");
        }
        if (projectDto.getDescription() != null) {
            if (projectDto.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
                throw new DataValidationException("Project's description length can't be more than 4096 symbols");
            }
        }
    }
}
