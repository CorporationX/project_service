package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
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
@Validated
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ProjectDto createProject(@RequestBody @Valid ProjectDto projectDto) {
        return projectService.createProject(projectDto);
    }

    @PutMapping("/updated")
    public ProjectDto updatedProject(@RequestBody @Valid ProjectDto projectDto) {
        return projectService.updatedProject(projectDto);
    }

    @PostMapping("/filter/{userId}")
    public List<ProjectDto> getAllAvailableProjectsForUserWithFilter(@NotBlank @RequestBody ProjectFilterDto filter,
                                                                     @PathVariable Long userId) {
        return projectService.getAllAvailableProjectsForUserWithFilter(filter, userId);
    }

    @GetMapping("/user/{userId}")
    public List<ProjectDto> getAllAvailableProjectsForUser(@PathVariable Long userId) {
        return projectService.getAllAvailableProjectsForUser(userId);
    }

    @GetMapping("/{projectId}")
    public ProjectDto getProjectById(@PathVariable Long projectId) {
        return projectService.getProjectById(projectId);
    }
}

