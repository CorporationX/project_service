package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectByFilterDto;
import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ProjectDto createProject(@Valid @RequestBody ProjectCreateDto projectCreateDto) {
        return projectService.createProject(projectCreateDto);
    }

    @PutMapping("/{id}")
    public ProjectDto updateProject(@PathVariable long id, @Valid @RequestBody ProjectUpdateDto projectUpdateDto) {
        return projectService.updateProject(id, projectUpdateDto);
    }

    @PostMapping("/filter/{id}")
    public List<ProjectDto> getAllProjectsByFilter(@PathVariable long id, @RequestBody ProjectByFilterDto projectByFilterDto) {
        return projectService.getAllProjectsByFilter(id, projectByFilterDto);
    }

    @GetMapping("/all")
    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public ProjectDto getProjectById(long id) {
        return projectService.getProjectById(id);
    }
}
