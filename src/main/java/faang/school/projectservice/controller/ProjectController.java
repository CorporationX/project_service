package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    public ProjectDto createProject(ProjectDto projectDto) {
        return projectService.createProject(projectDto);
    }

    public ProjectDto updateProject(long id, ProjectDto projectDto) {
        return projectService.updateProject(id, projectDto);
    }

    public List<ProjectDto> getAllProjectsByFilter(ProjectFilterDto filters) {
        return projectService.getAllProjectsByFilter(filters);
    }

    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProjects();
    }

    public ProjectDto findProjectById(long id) {
        return projectService.findProjectById(id);
    }
}
