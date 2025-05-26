package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    public ProjectDto createProject(
            @PathVariable long userId,
            @RequestBody ProjectDto projectDto) {
        return projectService.createProject(userId, projectDto);
    }

    public ProjectDto updateProject(
            @PathVariable long userId,
            @RequestBody ProjectDto projectDto) {
        return projectService.updateProject(userId, projectDto);
    }

    public List<ProjectDto> getProjectsByStatus(
            @PathVariable ProjectStatus projectStatus) {
        return projectService.getProjectsByStatus(projectStatus);
    }

    @GetMapping("/{projectId}/exists")
    public ResponseEntity<Void> checkProjectExists(@PathVariable @NotNull @Positive Long projectId) {
        projectService.checkProjectExists(projectId);
        return ResponseEntity.ok().build();
    }
}
