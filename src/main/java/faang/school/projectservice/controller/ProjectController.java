package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final UserContext userContext;

    @GetMapping
    public List<ProjectDto> getAllProjects() {
        return projectService.findAll();
    }


    @PostMapping
    public ProjectDto createProject(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.createProject(projectDto);

    }

    @PutMapping
    public ProjectDto updateProject(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.updateProject(projectDto);
    }

    @GetMapping("/{id}")
    public ProjectDto findById(@Positive @PathVariable Long id) {
        return projectService.findById(id);
    }

    @GetMapping("/filter")
    public List<ProjectDto> findByFilter(@RequestBody ProjectFilterDto filter) {
        return projectService.getAllProjectByFilters(filter);
    }
}
