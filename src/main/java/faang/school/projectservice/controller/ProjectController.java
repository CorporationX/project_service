package faang.school.projectservice.controller;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.project.ProjectServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
@Tag(name = "Project")
public class ProjectController {
    private final ProjectServiceImpl projectService;

    @Operation(summary = "Create project")
    @PostMapping
    public ProjectDto create(@Valid @NonNull @ParameterObject @RequestBody ProjectDto projectDto) {
        return projectService.create(projectDto);
    }

    @Operation(summary = "Update project")
    @PutMapping
    public ProjectDto update(@Valid @NonNull @ParameterObject @RequestBody ProjectDto projectDto) {
        return projectService.update(projectDto);
    }

    @Operation(summary = "Get all projects")
    @GetMapping
    public List<ProjectDto> getAll() {
        return projectService.getAll();
    }

    @Operation(summary = "Get project by projectId")
    @GetMapping("/{id}")
    public ProjectDto findById(@Positive @Parameter @PathVariable long id) {
        return projectService.findById(id);
    }

    @Operation(summary = "Get all projects by filter")
    @GetMapping("/filter")
    public List<ProjectDto> getAllByFilter(@Valid @NonNull @ParameterObject ProjectFilterDto projectFilterDto) {
        return projectService.getAllByFilter(projectFilterDto);
    }

    @Operation(summary = "Check is user project owner")
    @GetMapping("/{projectId}/owner/{userId}")
    public boolean checkProjectOwner(@PathVariable("projectId") long projectId,
                                     @PathVariable("userId") long userId) {
        return projectService.isUserProjectOwner(projectId, userId);
    }
}
