package faang.school.projectservice.controller;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping()
    public ProjectDto createProject(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.createProject(projectDto);
    }

    @PutMapping()
    public ProjectDto updateProject(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.updateProject(projectDto);
    }

    @GetMapping("/{id}")
    public ProjectDto findProjectById(@PathVariable Long id) {
        return projectService.findProjectById(id);
    }

    @GetMapping("/filter")
    public List<ProjectDto> getProjectByFilter(@RequestBody ProjectFilterDto filters) {
        return projectService.getProjectByFilter(filters);
    }

    @GetMapping()
    public List<ProjectDto> getAllProject() {
        return projectService.getAllProject();
    }
}
