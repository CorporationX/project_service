package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    public ProjectDto create(ProjectDto projectDto) {
        if (projectDto.getName() == null || projectDto.getName().isBlank()) {
            throw new RuntimeException("Invalid name " + projectDto.getName());
        }
        if (projectDto.getDescription() == null || projectDto.getDescription().isBlank()) {
            throw new RuntimeException("Invalid description " + projectDto.getDescription());
        }
        return projectService.create(projectDto);
    }

    public ProjectDto update(ProjectDto projectDto) {
        if (projectDto.getId() == null || projectDto.getId().equals(0L)) {
            throw new RuntimeException("Invalid id " + projectDto.getId());
        }
        return projectService.update(projectDto);
    }

    public List<ProjectDto> getProjectsWithFilters(ProjectDto projectDto) {
        return projectService.getProjectsWithFilters(projectDto);
    }

    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProjects();
    }
@GetMapping("project/{projectId}")
    public ProjectDto getProjectById(@PathVariable Long projectId) {
        if (projectId == null || projectId.equals(0L)) {
            throw new RuntimeException("Invalid id " + projectId);
        }
        return projectService.getProjectById(projectId);
    }

}
