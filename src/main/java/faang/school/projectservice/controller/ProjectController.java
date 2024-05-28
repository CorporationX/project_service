package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ProjectDto create(@RequestBody ProjectDto projectDto) {
        return projectService.create(projectDto);
    }

    @PutMapping("/{projectId}")
    public ProjectDto update(@PathVariable long projectId, ProjectDto projectDto) {
        return projectService.update(projectId, projectDto);
    }

    @GetMapping("/filtered")
    public List<ProjectDto> getFilteredProject(@RequestBody ProjectFilterDto filters) {
        return projectService.getFilteredProject(filters);
    }

    @GetMapping()
    public List<ProjectDto> getAllProject() {
        return projectService.getAllProject();
    }

    @GetMapping("/{projectId}")
    public ProjectDto getProjectById(@PathVariable long projectId) {
        return projectService.findById(projectId);
    }

    @DeleteMapping("/{projectId}")
    public void delete(@PathVariable long projectId) {
        projectService.delete(projectId);
    }
}
