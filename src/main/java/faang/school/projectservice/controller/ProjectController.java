package faang.school.projectservice.controller;

import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.List;
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class ProjectController {
    private ProjectService projectService;

    @PostMapping("/{userId}/project/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto create(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.createProject(projectDto);
    }

    @PutMapping("/{id}")
    public ProjectDto updateProject(@Valid @PathVariable Long projectId, @Valid @RequestBody ProjectDto projectDto) {
        return projectService.updateProject(projectId, projectDto);
    }

    @PostMapping("/filter/{id}")
    private List<ProjectDto> getProjectsByFilter(@RequestBody ProjectFilterDto filters, @PathVariable Long userId) {
        return projectService.getProjectsByFilter(filters);
    }

    @GetMapping("/all")
    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public ProjectDto getProjectById(@PathVariable Long projectId) {
        return projectService.getProjectById(projectId);
    }
}

