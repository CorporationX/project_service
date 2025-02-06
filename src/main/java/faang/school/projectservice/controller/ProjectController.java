package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.base-path}/project")
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable @Min(1L) Long projectId) {
        return projectService.getProject(projectId);
    }
}
