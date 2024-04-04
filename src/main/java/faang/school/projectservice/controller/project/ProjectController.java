package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
@Tag(name = "Projects", description = "Endpoints for managing projects")
public class ProjectController {

    private final ProjectService projectService;

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

    @Operation(summary = "Find project by project id")
    @GetMapping("/{projectId}")
    public ProjectDto findProjectById(@PathVariable @Positive(message = "id must be greater than zero") Long projectId) {
        return projectService.findProjectById(projectId);
    }

    @Operation(summary = "Adding a cover to a project")
    @PutMapping("/{projectId}/add")
    public ProjectDto addACoverToTheProject(@PathVariable @Positive(message = "id must be greater than zero") Long projectId, @RequestParam("file") MultipartFile file) {
        return projectService.addACoverToTheProject(projectId, file);
    }
}