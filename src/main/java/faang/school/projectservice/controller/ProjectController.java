package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto create(@PathVariable Long userId, @Validated @RequestBody ProjectDto projectDto) {
        return projectService.create(userId, projectDto);
    }

    @PutMapping("/")
    public ProjectDto update(@Validated @RequestBody ProjectDto projectDto) {
        return projectService.update(projectDto);
    }

    @PostMapping("/{userId}/accessible/filtered")
    public List<ProjectDto> findProjectsWithFilter(@PathVariable Long userId,
                                                   @Validated @NotNull @RequestBody ProjectFilterDto projectFilterDto) {
        return projectService.findProjectsWithFilter(userId, projectFilterDto);
    }

    @GetMapping("/{userId}/accessible/projects")
    public List<ProjectDto> findAllProjects(@PathVariable Long userId) {
        return projectService.findAllProjects(userId);
    }

    @PostMapping("/{userId}/accessible/project")
    public ProjectDto findById(@PathVariable Long userId,
                               @Validated @RequestBody ProjectFilterDto projectFilterDto) {
        return projectService.findById(userId, projectFilterDto);
    }
}
