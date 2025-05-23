package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.project.ProjectServiceImpl;
import lombok.Data;
import org.springframework.stereotype.Controller;

import java.util.List;

@Data
@Controller
public class ProjectController {

    private final ProjectServiceImpl projectService;

    public ProjectDto create(long userId, ProjectDto projectDto) {
        ProjectDto validatedDto = validate(userId, projectDto);
        return projectService.create(userId, validatedDto);
    }

    public ProjectDto update(long projectId, ProjectDto projectDto) {
        if (projectDto.getOwnerId() == null) {
            throw new DataValidationException("Project for updating should be found have ownerID. Fill in this field.");
        }
        return projectService.update(projectId, projectDto);
    }

    public List<ProjectDto> getFilteredProjects(long userId, ProjectFilterDto projectDto) {
        return projectService.getFilteredProjects(userId, projectDto);
    }

    public List<ProjectDto> getAllProjects(long userId) {
        ProjectFilterDto emptyDto = ProjectFilterDto.builder().build();
        return projectService.getFilteredProjects(userId, emptyDto);
    }

    public ProjectDto getProjectById(long userId, long projectId) {
        return projectService.getProjectById(userId, projectId);
    }

    private ProjectDto validate(long userId, ProjectDto projectDto) {
        if (projectDto.getName() == null
                || projectDto.getDescription() == null
                || projectDto.getName().isBlank()
                || projectDto.getDescription().isBlank()) {
            throw new DataValidationException("Every project should have a name and a description");
        }
        if (projectDto.getOwnerId() == null) {
            projectDto.setOwnerId(userId);
        }
        return projectDto;
    }
}
