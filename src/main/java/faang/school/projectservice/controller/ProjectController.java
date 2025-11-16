package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/project/{projectId}")
    ProjectDto getProject(@PathVariable long projectId) {
        return projectService.getProject(projectId);
    }
}
