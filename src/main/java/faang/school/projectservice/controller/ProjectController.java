package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.constraints.NotBlank;
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
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ProjectDto createProject(@NotBlank @RequestBody ProjectDto projectDto) {
        return projectService.createProject(projectDto);
    }

    @PutMapping("/updated")
    public ProjectDto updatedProject(@NotBlank @RequestBody ProjectDto projectDto) {
        return projectService.updatedProject(projectDto);
    }

    @PostMapping("/{userId}")
    public List<ProjectDto> getProjectWithFilter(
            @NotBlank @RequestBody ProjectFilterDto filter, @PathVariable long userId) {
        return projectService.getProjectWithFilters(filter, userId);
    }

    @GetMapping("/user/{userId}")
    public List<ProjectDto> getAllProject(@PathVariable long userId) {
        return projectService.getAllProject(userId);
    }

    @GetMapping("/project/{projectId}")
    public ProjectDto getProjectId(@NotBlank @PathVariable long projectId) {
        return projectService.getProjectId(projectId);
    }
}

