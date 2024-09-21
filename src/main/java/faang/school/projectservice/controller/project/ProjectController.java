package faang.school.projectservice.controller.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.project.ProjectServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {
    private final ProjectServiceImpl projectService;

    @GetMapping("/{projectId}")
    public Project getProject(@PathVariable long projectId) {
        return projectService.getProject(projectId);
    }
}
