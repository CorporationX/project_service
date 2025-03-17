package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/creating")
    public void createProject(@RequestParam Long userId, @RequestBody ProjectDto projectDto) {
        validateUserId(userId);
        validateCreatedProject(projectDto);
        projectService.createProject(userId, projectDto);
    }

    @PutMapping("/updating/{projectId}")
    public void updateProject(@PathVariable Long projectId, @RequestBody ProjectDto projectDto) {
        validateProjectId(projectId);
        validateUpdatedProject(projectDto);
        projectService.updateProject(projectId, projectDto);
    }

    @GetMapping("/filtered")
    public List<ProjectDto> findProjectsByFilters(@RequestParam Long userId,
                                                  @RequestBody ProjectFilterDto projectFilterDto) {
        validateUserId(userId);
        return projectService.findProjectsByFilters(userId, projectFilterDto);
    }

    @GetMapping("/all")
    public List<ProjectDto> getAllProjects(@RequestParam Long userId) {
        validateUserId(userId);
        return projectService.getAllProjects(userId);
    }

    @GetMapping("/{projectId}")
    public ProjectDto getProjectById(@RequestParam Long userId, @PathVariable Long projectId) {
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

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
    }

    private void validateCreatedProject(ProjectDto projectDto) {
        validateDescription(projectDto.description());
        if (projectDto.name() == null || projectDto.name().isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }

    private void validateUpdatedProject(ProjectDto projectDto) {
        validateDescription(projectDto.description());
    }
}
