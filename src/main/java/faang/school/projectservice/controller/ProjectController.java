package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    public void createProject(Long userId, ProjectDto projectDto) {
        projectService.createProject(userId, projectDto);
    }

    public void updateProjectStatus(Long userId, Long projectId, ProjectStatus status) {
        projectService.updateProjectStatus(userId, projectId, status);
    }

    public void updateProjectDescription(Long userId, Long projectId, String description) {
        projectService.updateProjectDescription(userId, projectId, description);
    }

    public List<ProjectDto> findProjectsByFilters(Long userId, ProjectFilterDto projectFilterDto) {
        return projectService.findProjectsByFilters(userId, projectFilterDto);
    }

    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProjects();
    }

    public ProjectDto getProjectById(Long projectId) {
        return projectService.getProjectById(projectId);
    }

    //методы валидации
}
