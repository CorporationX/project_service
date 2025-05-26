package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/users/{userId}")
    public ResponseEntity<ProjectDto> createProject(
            @PathVariable @Positive long userId,
            @RequestBody @Valid ProjectDto projectDto) {
        log.info("Creating project for user {}", userId);
        ProjectDto createdProject = projectService.createProject(userId, projectDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<ProjectDto> updateProject(
            @PathVariable @Positive long userId,
            @RequestBody @Valid ProjectDto projectDto) {
        log.info("Updating project for user {}", userId);
        return ResponseEntity.ok(projectService.updateProject(userId, projectDto));
    }

    @GetMapping("/status/{projectStatus}")
    public ResponseEntity<List<ProjectDto>> getProjectsByStatus(
            @PathVariable ProjectStatus projectStatus) {
        log.info("Getting projects with status {}", projectStatus);
        return ResponseEntity.ok(projectService.getProjectsByStatus(projectStatus));
    }

    @GetMapping("/{projectId}/exists")
    public ResponseEntity<Void> checkProjectExists(
            @PathVariable @NotNull @Positive Long projectId) {
        log.info("Checking existence of project {}", projectId);
        projectService.checkProjectExists(projectId);
        return ResponseEntity.ok().build();
    }
}
