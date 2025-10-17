package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Validated
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody ProjectCreateDto projectDto) {
        projectService.create(projectDto);
    }

    @PutMapping("/{projectId}")
    public ProjectDto update(@Valid @RequestBody ProjectUpdateDto projectDto,
                             @PathVariable @Positive(message = "Project id must be positive") long projectId) {
        return projectService.update(projectDto, projectId);
    }

    @GetMapping("/{projectId}")
    public ProjectDto getById(@PathVariable @Positive(message = "Project id must be positive") long projectId) {
        return projectService.getById(projectId);
    }

    @GetMapping
    public List<ProjectDto> getAll(@RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer size,
                                   @RequestParam(required = false) String sortBy,
                                   @RequestParam(required = false) String sortDirection
    ) {
        ProjectFilterDto filterDto = ProjectFilterDto.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        return projectService.getByFilter(filterDto);
    }

    @PostMapping("/filter")
    public List<ProjectDto> getAllByFilter(@Valid @RequestBody ProjectFilterDto filterDto) {
        return projectService.getByFilter(filterDto);
    }

    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Positive(message = "Project id must be positive") @PathVariable long projectId) {
        projectService.delete(projectId);
    }
}
