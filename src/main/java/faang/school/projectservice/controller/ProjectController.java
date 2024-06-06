package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/project/{projectId}")
    public ProjectDto getProject(@PathVariable long projectId) {
        return projectService.getProjectById(projectId);
    }

    @GetMapping("/project/{projectId}/exists")
    public boolean existsById(@PathVariable long projectId) {
        return projectService.existsById(projectId);
    }
}
