package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectDtoFilter;
import faang.school.projectservice.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Endpoint for managing projects")
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "Create project in database")
    @PostMapping("/create")
    public ProjectDto createProject(@RequestBody ProjectDto projectDto) {
        return projectService.createProject(projectDto);
    }

    @Operation(summary = "Update project in database")
    @PutMapping("/update")
    public ProjectDto updateProject(@RequestBody ProjectDto projectDto) {
        return projectService.updateProject(projectDto);
    }

    @Operation(summary = "Get filtered projects")
    @PostMapping("/filter/{userId}")
    public List<ProjectDto> findProjectByFilters(@PathVariable("userId") Long userId, @RequestBody ProjectDtoFilter projectDtoFilter) {
        return projectService.findProjectByFilters(userId, projectDtoFilter);
    }

    @Operation(summary = "Get all project")
    @GetMapping("/all")
    public List<ProjectDto> findAllProjects() {
        return projectService.findAllProjects();
    }

    @Operation(summary = "Get project by id")
    @GetMapping("/{projectId}")
    public ProjectDto findProjectById(@PathVariable("projectId") Long projectId) {
        return projectService.findProjectById(projectId);
    }
}
