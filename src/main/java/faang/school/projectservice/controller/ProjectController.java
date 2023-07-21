package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class ProjectController {
    private final ProjectService projectService;

    public ProjectDto create(ProjectDto projectDto) {
        validateCreateProject(projectDto);
        return projectService.create(projectDto);
    }

    public ProjectDto update(ProjectDto projectDto, long projectId) {
        validateUpdateProject(projectDto, projectId);
        return projectService.update(projectDto, projectId);
    }

    public List<ProjectDto> getProjectWithFilters(ProjectFilterDto projectFilterDto, List<Team> userTeams) {
        return projectService.getProjectsWithFilter(projectFilterDto, userTeams);
    }

    public List<ProjectDto> getAllProjects(List<Team> userTeams) {
        return projectService.getAllProjects(userTeams);
    }

    private void validateCreateProject(ProjectDto projectDto) {
        if (projectDto.getName() == null || projectDto.getName().isBlank()) {
            throw new DataValidationException("Project can't be created with empty name");
        }
        if (projectDto.getName().length() > 128) {
            throw new DataValidationException("Project's name length can't be more than 128 symbols");
        }
        if (projectDto.getDescription() == null || projectDto.getDescription().isBlank()) {
            throw new DataValidationException("Project can't be created with empty description");
        }
        if (projectDto.getDescription().length() > 4096) {
            throw new DataValidationException("Project's description length can't be more than 4096 symbols");
        }
    }

    private void validateUpdateProject(ProjectDto projectDto, long projectId) {
        if (projectId <= 0) {
            throw new DataValidationException("Id can't be negative or zero");
        }
        if (projectDto.getName().isBlank()) {
            throw new DataValidationException("Project can't be created with empty name");
        }
        if (projectDto.getName().length() > 128) {
            throw new DataValidationException("Project's name length can't be more than 128 symbols");
        }
        if (projectDto.getDescription().isBlank()) {
            throw new DataValidationException("Project can't be created with empty description");
        }
        if (projectDto.getDescription().length() > 4096) {
            throw new DataValidationException("Project's description length can't be more than 4096 symbols");
        }
    }
}
