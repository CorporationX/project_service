package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService service;

    @GetMapping("/project/{projectId}")
    public ProjectDto getProject(@PathVariable long projectId) {
        return service.getProject(projectId);
    }

    @PostMapping("/projects")
    public List<ProjectDto> getProjectsByIds(@RequestBody List<Long> ids) {
        return service.getProjectsByIds(ids);
    }
}
