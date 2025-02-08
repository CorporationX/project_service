package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.CreateProjectRequest;
import faang.school.projectservice.dto.project.DeleteProjectRequest;
import faang.school.projectservice.dto.project.FilterProjectRequest;
import faang.school.projectservice.dto.project.ProjectResponse;
import faang.school.projectservice.dto.project.UpdateProjectRequest;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ProjectResponse createProject(@Valid @RequestBody CreateProjectRequest createProjectRequest) {
        return projectService.createProject(createProjectRequest);
    }

    @PutMapping
    public ProjectResponse updateProject(@Valid @RequestBody UpdateProjectRequest updateProjectRequest) {
        return projectService.updateProject(updateProjectRequest);
    }

    @PostMapping("/{userId}/search")
    public List<ProjectResponse> filterProjects(@Valid @Positive @PathVariable Long userId,
                                                @RequestBody FilterProjectRequest filterProjectRequest) {
        return projectService.filterProjects(userId, filterProjectRequest);
    }

    @GetMapping("/{userId}/all")
    public List<ProjectResponse> getProjects(@Valid @Positive @PathVariable Long userId) {
        return projectService.getAllProjects(userId);
    }

    @DeleteMapping
    public void deleteProject(@Valid @RequestBody DeleteProjectRequest deleteProjectRequest) {
        projectService.deleteProject(deleteProjectRequest);
    }
}

