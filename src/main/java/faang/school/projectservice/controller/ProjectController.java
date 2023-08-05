package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ProjectDto create(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.create(projectDto);
    }

    @PutMapping("/update")
    public ProjectDto update(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.update(projectDto);
    }

    @PostMapping("/{userId}")
    public List<ProjectDto> getProjectWithFilters(@Valid @RequestBody ProjectFilterDto projectFilterDto, @PathVariable long userId) {
        return projectService.getProjectsWithFilter(projectFilterDto, userId);
    }

    @GetMapping("/project/{userId}")
    public List<ProjectDto> getAllProjects(@PathVariable long userId) {
        return projectService.getAllProjects(userId);
    }

    @GetMapping("/project")
    public ProjectDto getProjectById(@RequestParam("projectId") long projectId, @RequestParam("userId") long userId) {
        return projectService.getProjectById(projectId, userId);
    }
}
