package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.filter.ProjectFilterDto;
import faang.school.projectservice.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/project")
@Tag(name = "Projects", description = "Project handler")
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "Create new project")
    @PostMapping("/create")
    public ProjectDto create(@RequestHeader(value = "x-user-id") String userid,
                             @RequestBody ProjectDto projectDto) {
        if (projectDto.getName() == null || projectDto.getName().isBlank()) {
            throw new RuntimeException("Invalid name " + projectDto.getName());
        }
        if (projectDto.getDescription() == null || projectDto.getDescription().isBlank()) {
            throw new RuntimeException("Invalid description " + projectDto.getDescription());
        }
        return projectService.create(projectDto);
    }

    @Operation(summary = "Update project", description = "Update status and/or description")
    @PutMapping("/update")
    public ProjectDto update(@RequestBody ProjectDto projectDto) {

        if (projectDto.getId() == null || projectDto.getId().equals(0L)) {
            throw new RuntimeException("Invalid id " + projectDto.getId());
        }
        return projectService.update(projectDto);
    }

    @Operation(summary = "Get projects by filter")
    @PostMapping("/getByFilters")
    public List<ProjectDto> getProjectsWithFilters(@RequestHeader(value = "x-user-id") String userid,
                                                   @RequestBody ProjectFilterDto filters) {
        return projectService.getProjectsWithFilters(filters);
    }

    @Operation(summary = "Get all projects")
    @GetMapping("/getAll")
    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProjects();
    }

    @Operation(summary = "Get projects by id")
    @GetMapping("/{projectId}")
    public ProjectDto getProjectById(@PathVariable Long projectId) {
        if (projectId == null || projectId.equals(0L)) {
            throw new RuntimeException("Invalid id " + projectId);
        }
        return projectService.getProjectById(projectId);
    }

}
