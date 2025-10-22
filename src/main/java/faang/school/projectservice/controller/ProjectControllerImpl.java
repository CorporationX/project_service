package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Validated
@Tag(name = "Projects", description = "API для управления проектами")
public class ProjectControllerImpl implements ProjectController {
    private final ProjectService projectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto create(@Valid @RequestBody ProjectCreateDto projectDto) {
        return projectService.create(projectDto);
    }

    @PutMapping("/{projectId}")
    public ProjectDto update(
            @Valid @RequestBody ProjectUpdateDto projectDto,
            @Parameter(description = "ID проекта", required = true)
            @PathVariable @Positive long projectId) {
        return projectService.update(projectDto, projectId);
    }

    @GetMapping("/{projectId}")
    public ProjectDto getById(
            @Parameter(description = "ID проекта", required = true)
            @PathVariable @Positive long projectId) {
        return projectService.getById(projectId);
    }

    @GetMapping
    public List<ProjectDto> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {

        ProjectFilterDto filterDto = ProjectFilterDto.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
        return projectService.getByFilter(filterDto);
    }

    @PostMapping("/search")
    public List<ProjectDto> search(@Valid @RequestBody ProjectFilterDto filterDto) {
        return projectService.getByFilter(filterDto);
    }

    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable @Positive long projectId) {
        projectService.delete(projectId);
    }
}
