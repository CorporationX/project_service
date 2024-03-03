package faang.school.projectservice.controller;

import faang.school.projectservice.api.ProjectApi;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController implements ProjectApi {
    private final ProjectService projectService;

    @Override
    @PostMapping
    public ProjectDto create(@RequestBody @Valid ProjectDto projectDto) {
        return projectService.create(projectDto);
    }

    @Override
    @PutMapping
    public ProjectDto update(@RequestBody ProjectDto projectDto) {
        return projectService.update(projectDto);
    }

    @Override
    @GetMapping("/all")
    public List<ProjectDto> getAll() {
        return projectService.getAll();
    }

    @GetMapping("/sub/{parentId}")
    public List<ProjectDto> getAllSubprojectsByFilter(@PathVariable @Min(1) long parentId,
                                                      @ModelAttribute ProjectFilterDto filterDto) {
        return projectService.getAllSubprojectsByFilter(parentId, filterDto);
    }

    @Override
    @GetMapping("/{projectId}")
    public ProjectDto getById(@PathVariable @Min(1) long projectId) {
        return projectService.getProjectDtoById(projectId);
    }

    @Override
    @GetMapping("/filters")
    public List<ProjectDto> getByFilters(@ModelAttribute ProjectFilterDto filterDto) {
        return projectService.getAll(filterDto);
    }

    @PostMapping("/exists/{projectId}")
    public boolean existsProjectById(@PathVariable long projectId) {
        return projectService.existsProjectById(projectId);
    }
}