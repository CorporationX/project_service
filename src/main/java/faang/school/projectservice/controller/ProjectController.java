package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectFilterDto;
import lombok.RequiredArgsConstructor;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping
    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public ProjectDto getProject(@PathVariable("id") long projectId) {
        return projectService.getProject(projectId);
    }

    @PostMapping()
    public List<ProjectDto> getProjects(@RequestBody ProjectFilterDto projectFilter) {
        return projectService.getProjects(projectFilter);
    }

    @PostMapping("/project")
    public ProjectDto createProject(@RequestBody ProjectDto projectDto) {
        return projectService.createProject(projectDto);
    }

    @PutMapping("/project/{projectId}")
    public ProjectDto updateProject(@RequestBody ProjectDto projectDto,
                                    @PathVariable long projectId) {
        return projectService.updateProject(projectDto, projectId);
    }
}
