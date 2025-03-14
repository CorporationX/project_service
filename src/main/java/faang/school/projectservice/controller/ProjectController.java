package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    public void createProject(ProjectDto projectDto) {
        validateProjectId(projectDto.id());
        validateCreatedProject(projectDto);
        projectService.createProject(projectDto);
    }

    public void updateProject(ProjectDto projectDto) {
        validateProjectId(projectDto.id());
        validateCreatedProject(projectDto);
        projectService.updateProject(projectDto);
    }

    public List<ProjectDto> findProjectsByFilters(Long userId, ProjectFilterDto projectFilterDto) {
        validateUserId(userId);
        return projectService.findProjectsByFilters(userId, projectFilterDto);
    }

    public List<ProjectDto> getAllProjects(Long userId) {
        validateUserId(userId);
        return projectService.getAllProjects(userId);
    }

    public ProjectDto getProjectById(Long userId, Long projectId) {
        validateUserId(userId);
        validateProjectId(projectId);
        return projectService.getProjectById(userId, projectId);
    }

    private void validateProjectId(Long projectId) {
        Objects.requireNonNull(projectId, "Project must contain id");
    }

    private void validateUserId(Long userId) {
        Objects.requireNonNull(userId, "User must contain id");
    }

    private void validateCreatedProject(ProjectDto projectDto) {
        Objects.requireNonNull(projectDto.ownerId(), "Invalid owner id value");
        if (projectDto.description() == null || projectDto.description().isBlank()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (projectDto.name() == null || projectDto.name().isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }
}
