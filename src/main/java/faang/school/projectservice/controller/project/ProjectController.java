package faang.school.projectservice.controller.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.project.ProjectServiceImpl;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project")
@Validated
public class ProjectController {
    private final ProjectServiceImpl projectService;

    @GetMapping("/{projectId}")
    public Project getProject(@PathVariable @Positive long projectId) {
        return projectService.getProject(projectId);
    }
}
