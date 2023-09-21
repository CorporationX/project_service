package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
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
@RequestMapping("/api/v1/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final UserContext userContext;

    @GetMapping
    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProjects(userContext.getUserId());
    }

    @GetMapping("/{id}")
    public ProjectDto getProject(@PathVariable("id") long projectId) {
        return projectService.getProject(projectId);
    }
  
    @PostMapping("/project")
    public ProjectDto createProject(@RequestBody ProjectDto projectDto) {
        return projectService.createProject(projectDto);
    }

    @PutMapping("/{projectId}")
    public ProjectDto updateProject(@RequestBody ProjectDto projectDto,
                                    @PathVariable("projectId") long projectId) {
        return projectService.updateProject(projectDto, projectId);
    }

    @PostMapping
    public List<ProjectDto> getProjects(@Valid @RequestBody ProjectFilterDto projectFilter) {
        return projectService.getProjects(projectFilter);
    }

    @GetMapping("/exists/{id}")
    public Boolean isProjectExist(@PathVariable long id) {
        return projectService.isProjectExist(id);
    }
}