package faang.school.projectservice.controller.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserContext userContext;

    @PostMapping
    public ProjectDto addProject(@RequestBody CreateProjectDto projectDto) {
        return projectService.create(userContext.getUserId(), projectDto);
    }

    @PutMapping("/{projectId}")
    public ProjectDto updateProject(
            @PathVariable long projectId,
            @RequestBody UpdateProjectDto projectDto
    ) {
        return projectService.update(userContext.getUserId(), projectId, projectDto);
    }

    @GetMapping("/{projectId}")
    public ProjectDto getById(@PathVariable long projectId) {
        return projectService.getById(userContext.getUserId(), projectId);
    }

    @GetMapping
    public List<ProjectDto> getAll() {
        return projectService.getAll(userContext.getUserId());
    }

    @GetMapping("/search")
    public List<ProjectDto> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ProjectStatus status
    ) {
        return projectService.search(userContext.getUserId(), name, status);
    }

}
