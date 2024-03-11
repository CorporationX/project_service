package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Endpoint for managing projects")
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "Create project by user id")
    @PostMapping("/project")
    public ProjectDto createProject(@RequestHeader Long userId, @RequestBody ProjectDto projectDto) {
        return projectService.createProject(userId, projectDto);
    }

    @Operation(summary = "Update project by project id")
    @PutMapping("/project/{projectId}")
    public ProjectDto updateProject(@PathVariable Long projectId, @RequestBody ProjectDto projectDto) {
        return projectService.updateProject(projectId, projectDto);
    }

    @Operation(summary = "Find all user projects by filters")
    @PostMapping("/project/filters")
    public List<ProjectDto> findAllProjectsByFilters(@RequestHeader Long userId, @RequestBody ProjectFilterDto filters) {
        return projectService.findAllProjectsByFilters(userId, filters);
    }

    @Operation(summary = "Find all projects")
    @GetMapping("/project")
    public List<ProjectDto> findAllProjects() {
        return projectService.findAllProjects();
    }

    @Operation(summary = "Find all projects by projects id's")
    @PostMapping("/project/ids")
    public List<ProjectDto> findAllProjectsByIds(@RequestBody List<Long> projectIds) {
        return projectService.findAllProjectsByIds(projectIds);
    }
}