package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.project_service.ProjectServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectServiceImpl projectService;

    public void createProject(ProjectDto projectDto) {
        projectService.createProject(projectDto);
    }

    public void updateStatus(ProjectDto projectDto, ProjectStatus status) {
        projectService.updateStatus(projectDto, status);
    }

    public void updateDescription(ProjectDto projectDto, String description) {
        projectService.updateDescription(projectDto, description);
    }

    public List<ProjectDto> getProjectsFilters(ProjectFilterDto filterDto) {
        return projectService.getProjectsFilters(filterDto);
    }

    public List<ProjectDto> getProjects() {
        return projectService.getProjects();
    }

    public ProjectDto findById(long id) {
        return projectService.findById(id);
    }
}
