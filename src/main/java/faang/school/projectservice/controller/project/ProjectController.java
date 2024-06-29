package faang.school.projectservice.controller.project;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("projects")
public class ProjectController {
    private final ProjectService projectService;
    
    @GetMapping
    public List<ProjectDto> getAllProjects() {
        return projectService.getAll();
    }
    
    @GetMapping("/{id}")
    public ProjectDto getProject(@PathVariable @NotNull Long id) {
        return projectService.getById(id);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto createProject(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.create(projectDto);
    }

    @PutMapping
    public ProjectDto updateProject(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.update(projectDto);
    }
    
    @PostMapping("search")
    public List<ProjectDto> search(@Valid @RequestBody ProjectFilterDto filter) {
        return projectService.search(filter);
    }
    
    @GetMapping("/{projectId}/exists")
    public boolean existsById(@PathVariable long projectId) {
        return projectService.existsById(projectId);
    }

    @PostMapping("/{parentProjectId}/search")
    public List<ProjectDto> searchSubprojects(
        @Valid @RequestBody ProjectFilterDto filter,
        @PathVariable Long parentProjectId
    ) {
        return projectService.searchSubprojects(parentProjectId, filter);
    }
}
