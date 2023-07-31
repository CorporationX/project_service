package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.ProjectService;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ProjectDto createProject(@RequestBody ProjectDto projectDto) {
        projectValidate(projectDto);
        return projectService.createProject(projectDto);
    }

    @PutMapping("/{id}")
    public ProjectDto updateProject(@PathVariable long id, @RequestBody ProjectDto projectDto) {
        projectValidate(projectDto);
        return projectService.updateProject(id, projectDto);
    }

    @GetMapping("/all/{id}")
    public List<ProjectDto> getAllProjectsByStatus(@PathVariable long id, @RequestBody ProjectDto projectDto) {
        projectValidate(projectDto);
        return projectService.getAllProjectsByStatus(id, projectDto);
    }

    @GetMapping
    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public ProjectDto getProjectById(long id) {
        return projectService.getProjectById(id);
    }

    private void projectValidate(ProjectDto projectDto) {
        if (projectDto == null) {
            throw new DataValidationException("ProjectDto is null");
        }
        if (projectDto.getName() == null || projectDto.getName().isBlank() && projectDto.getName().isEmpty()) {
            throw new DataValidationException("Project name is empty");
        }
        if (projectDto.getDescription() == null || projectDto.getDescription().isBlank() && projectDto.getDescription().isEmpty()) {
            throw new DataValidationException("Project description is empty");
        }
        if (projectDto.getOwnerId() == null) {
            throw new DataValidationException("Project owner id is empty");
        }
    }
}
