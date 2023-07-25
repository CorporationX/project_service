package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    public ProjectDto createProject(ProjectDto projectDto) {
        projectValidate(projectDto);
        return projectService.createProject(projectDto);
    }

    public void updateProject(ProjectDto projectDto) {
        projectValidate(projectDto);
        projectService.updateProject( projectDto);
    }

    public void getAllProjectsByStatus(ProjectDto projectDto) {
        projectValidate(projectDto);
        projectService.getAllProjectsByStatus(projectDto);
    }

    public void getAllProjects() {
        projectService.getAllProjects();
    }

    public ProjectDto getProjectById(Long id) {
        validateId(id);
        return projectService.getProjectById(id);
    }

    private void projectValidate(ProjectDto projectDto) {
        if (projectDto == null) {
            throw new DataValidationException("ProjectDto is null");
        }
        if (projectDto.getName() == null || projectDto.getName().isBlank() && projectDto.getName().isEmpty()) {
            throw new DataValidationException("Project name is empty");
        }
        if (projectDto.getDescription() == null || projectDto.getDescription().isBlank() && projectDto.getDescription().isEmpty()) {
            throw new DataValidationException("Project description is empty");
        }
        if (projectDto.getOwnerId() == null) {
            throw new DataValidationException("Project owner id is empty");
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new DataValidationException("Project id is null");
        }
    }
}
