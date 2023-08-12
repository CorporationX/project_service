package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectFilterDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name="Projects", description = "API для управления проектами.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    @Operation(
            summary = "Получить все проекты",
            tags = { "projects", "get" })
    @GetMapping
    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProjects();
    }

    @Operation(
            summary = "Получить проект по ID",
            tags = { "projects", "get" })
    @GetMapping("/{id}")
    public ProjectDto getProject(@PathVariable("id") long projectId) {
        return projectService.getProject(projectId);
    }

    @Operation(
            summary = "Получить проекты с применением фильтров",
            tags = { "projects", "post" })
    @PostMapping
    public List<ProjectDto> getProjects(@RequestBody ProjectFilterDto projectFilter) {
        return projectService.getProjects(projectFilter);
    }

    @Operation(
            summary = "Создать проект",
            tags = { "projects", "post" })
    @PostMapping("/project")
    public ProjectDto createProject(@RequestBody ProjectDto projectDto) {
        return projectService.createProject(projectDto);
    }

    @Operation(
            summary = "Обновить проект",
            tags = { "projects", "put" })
    @PutMapping("/project/{projectId}")
    public ProjectDto updateProject(@RequestBody ProjectDto projectDto,
                                    @PathVariable long projectId) {
        return projectService.updateProject(projectDto, projectId);
    }
}
