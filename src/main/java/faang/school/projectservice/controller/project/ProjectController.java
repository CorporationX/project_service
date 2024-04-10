package faang.school.projectservice.controller.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.event.ProjectViewEvent;
import faang.school.projectservice.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
@Tag(name = "Projects", description = "Endpoints for managing projects")
public class ProjectController {

    private final UserContext userContext;
    private final ProjectService projectService;
    private final ProjectViewEvent projectViewEvent;

    @Operation(summary = "Create project")
    @PostMapping
    public ProjectDto createProject(@RequestHeader("userId") Long userId, @RequestBody @Valid ProjectDto projectDto) {
        return projectService.createProject(userId, projectDto);
    }

    @Operation(summary = "Update project")
    @PutMapping("/{projectId}")
    public ProjectDto updateProject(@PathVariable @Positive(message = "id must be greater than zero") Long projectId,
                                    @RequestBody @Valid ProjectDto projectDto) {
        return projectService.updateProject(projectId, projectDto);
    }

    @Operation(summary = "Find all user projects by filters")
    @PostMapping("/filters")
    public List<ProjectDto> findAllProjectsByFilters(@RequestHeader("userId") @Positive(message = "id must be greater than zero") Long userId,
                                                     @RequestBody ProjectFilterDto filters) {
        return projectService.findAllProjectsByFilters(userId, filters);
    }

    @Operation(summary = "Find all projects")
    @GetMapping()
    public List<ProjectDto> findAllProjects() {
        return projectService.findAllProjects();
    }

    @Async
    @Cacheable(cacheNames = "projectId", key = "#projectId")
    @Operation(summary = "Find project by project id")
    @GetMapping("/{projectId}")
    public ProjectDto findProjectById(@PathVariable @Positive(message = "id must be greater than zero") Long projectId) {
        Long userId = userContext.getUserId();
        LocalDateTime timestamp = LocalDateTime.now();
        projectViewEvent.publishProjectViewEvent(userId, projectId, timestamp);
        log.info("Project viewed: userId={}, projectId={}, timestamp={}", userId, projectId, timestamp);
        return projectService.findProjectById(projectId);
    }
}