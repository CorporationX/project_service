package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.updater.ProjectUpdaterDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    public ProjectDto create(long ownerId, String name, String description) {
        if (name == null || name.isEmpty()) {
            throw new RuntimeException("Invalid name " + name);
        }
        if (description == null || description.isEmpty()) {
            throw new RuntimeException("Invalid description " + description);
        }
        return projectService.create(ownerId, name, description);
    }

    public ProjectDto update(long id, ProjectStatus status) {
        if (status == null) {
            throw new RuntimeException("Status cannot be null");
        }
        ProjectUpdaterDto updater = ProjectUpdaterDto.builder()
                .status(status)
                .build();
        return projectService.update(id, updater);
    }

    public ProjectDto update(long id, String description) {
        if (description == null || description.isEmpty()) {
            throw new RuntimeException("Invalid description " + description);
        }
        ProjectUpdaterDto updater = ProjectUpdaterDto.builder()
                .description(description)
                .build();
        return projectService.update(id, updater);
    }

    public ProjectDto update(long id, ProjectStatus status, String description) {
        if (description == null || description.isEmpty()) {
            throw new RuntimeException("Invalid description " + description);
        }
        if (status == null) {
            throw new RuntimeException("Status cannot be null");
        }
        ProjectUpdaterDto updater = ProjectUpdaterDto.builder()
                .description(description)
                .status(status)
                .build();
        return projectService.update(id, updater);
    }

    public List<ProjectDto> getProjectsWithFilters(long userId, String name) {
        if (name == null || name.isEmpty()) {
            throw new RuntimeException("Invalid name " + name);
        }
        ProjectFilterDto filter = ProjectFilterDto.builder()
                .name(name)
                .build();
        return projectService.getProjectsWithFilters(userId, filter);
    }

    public List<ProjectDto> getProjectsWithFilters(long userId, ProjectStatus status) {
        if (status == null) {
            throw new RuntimeException("Status cannot be null");
        }
        ProjectFilterDto filter = ProjectFilterDto.builder()
                .status(status)
                .build();
        return projectService.getProjectsWithFilters(userId, filter);
    }

    public List<ProjectDto> getProjectsWithFilters(long userId, String name, ProjectStatus status) {
        if (status == null) {
            throw new RuntimeException("Status cannot be null");
        }
        ProjectFilterDto filter = ProjectFilterDto.builder()
                .status(status)
                .name(name)
                .build();
        return projectService.getProjectsWithFilters(userId, filter);
    }

    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProjects();
    }

    public ProjectDto getProjectById(long id) {
        return projectService.getProjectById(id);
    }

}
