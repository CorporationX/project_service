package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    public ProjectDto create(ProjectDto projectDto) {
        validateProject(projectDto);
        return projectService.create(projectDto);
    }

    public ProjectDto changeStatus(ProjectDto projectDto, Long id) {
        validateProject(projectDto);
        return projectService.updateStatusAndDescription(projectDto, id);
    }

    public List<ProjectDto> getProjectsByName(ProjectFilterDto projectFilterDto) {
        if (projectFilterDto.getName().isBlank()) {
            throw new DataValidationException("Project name is empty");
        }
        return projectService.getProjectByName(projectFilterDto);
    }

    public List<ProjectDto> getProjectsByStatus(ProjectFilterDto projectFilterDto) {
        if (projectFilterDto.getStatus() == null) {
            throw new DataValidationException("Project status is empty");
        }
        return projectService.getProjectByStatus(projectFilterDto);
    }

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
