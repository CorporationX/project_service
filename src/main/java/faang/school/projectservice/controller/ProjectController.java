package faang.school.projectservice.controller;

import faang.school.projectservice.ProjectService;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
@RequiredArgsConstructor
@Component
public class ProjectController {
    private ProjectService projectService;

    public ProjectDto create(ProjectDto projectDto) {
        return projectService.create(projectDto);
    }

    public ProjectDto updateProject(Long projectId, ProjectDto project) {
        return projectService.updateProject(projectId, project);
    }

    private List<ProjectDto> getProjectsByFilter(ProjectFilterDto filters, Long userId) {
        return projectService.getProjectsByFilter(filters, userId);
    }

    public List<ProjectDto> getAllProjects(Long userId) {
        return projectService.getAllProjects(userId);
    }

    public ProjectDto getProjectById(Long projectId) {
        return projectService.getProjectById(projectId);
    }
}

