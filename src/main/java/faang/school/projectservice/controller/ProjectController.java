package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.project.ProjectServiceImpl;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProjectController {

    private ProjectServiceImpl projectService;

    public ProjectDto create(long userId, ProjectDto projectDto) {
        projectDto = validate(userId, projectDto);
        return projectService.create(userId, projectDto);
    }

    public ProjectDto update(long userId, ProjectDto projectDto) {
        projectDto = validate(userId, projectDto);
        return projectService.update(userId, projectDto);
    }

    public List<ProjectDto> getFilteredProjects(long userId, ProjectDto projectDto) {
        projectDto = validate(userId, projectDto);
        return projectService.getFilteredProjects(userId, projectDto);
    }

    public List<ProjectDto> getAllProjects(long userId) {
        return projectService.getAllProjects(userId);
    }

    public ProjectDto getProjectById (long userId, long projectId){
        return projectService.getProjectById(userId, projectId);
    }

    private ProjectDto validate(long userId, ProjectDto projectDto){
        if (projectDto.getName() == null || projectDto.getDescription() == null) {
            throw new DataValidationException("Every project should have a name and a description");
        }
        if (projectDto.getOwnerId() == null) {
            projectDto.setOwnerId(userId);
        }
        return projectDto;
    }
}
