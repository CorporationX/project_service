package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/create")
    public ProjectDto createProject(@RequestBody ProjectDto projectDto) {
        return projectService.createProject(projectDto);
    }

    @PutMapping("/project/{projectId}")
    public ProjectDto updateProject(@RequestBody ProjectDto projectDto,
                                    @PathVariable long projectId) {
        return projectService.updateProject(projectDto, projectId);
    }
    @PostMapping
    public List<ProjectDto> getProjects(@RequestBody ProjectFilterDto projectFilter) {
        return projectService.getProjects(projectFilter);
    }
}