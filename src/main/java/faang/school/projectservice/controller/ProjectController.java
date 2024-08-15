package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Project")
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/projects")
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "All projects")
    @GetMapping
    public List<ProjectDto> getAllProjects() {
        return projectService.findAll();
    }

    @Operation(summary = "Create project")
    @PostMapping
    public ProjectDto createProject(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.createProject(projectDto);
    }

    @Operation(summary = "Update project")
    @PutMapping
    public ProjectDto updateProject(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.updateProject(projectDto);
    }

    @Operation(summary = "Find project by id")
    @GetMapping("/{id}")
    public ProjectDto findById(@Positive @PathVariable Long id) {
        return projectService.findById(id);
    }

    @Operation(summary = "Find all project by filter")
    @GetMapping("/filter")
    public List<ProjectDto> findByFilter(@RequestBody ProjectFilterDto filter) {
        return projectService.getAllProjectByFilters(filter);
    }
}
