package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectByFilterDto;
import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
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
    public ProjectDto createProject(@RequestBody ProjectCreateDto projectCreateDto) {
        projectValidate(projectCreateDto);
        return projectService.createProject(projectCreateDto);
    }

    @PutMapping("/{id}")
    public ProjectDto updateProject(@PathVariable long id, @RequestBody ProjectUpdateDto projectUpdateDto) {
        return projectService.updateProject(id, projectUpdateDto);
    }

    @PostMapping("/filter/{id}")
    public List<ProjectDto> getAllProjectsByFilter(@PathVariable long id, @RequestBody ProjectByFilterDto projectByFilterDto) {
        validateProjectFilter(projectByFilterDto);
        return projectService.getAllProjectsByFilter(id, projectByFilterDto);
    }

    @GetMapping("/all")
    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public ProjectDto getProjectById(long id) {
        return projectService.getProjectById(id);
    }

    private void validateProjectFilter(ProjectByFilterDto projectByFilterDto) {
        if (projectByFilterDto == null) {
            throw new DataValidationException("ProjectByFilterDto is null");
        }
    }

    private void projectValidate(ProjectCreateDto projectCreateDto) {
        if (projectCreateDto == null) {
            throw new DataValidationException("ProjectDto is null");
        }
        if (projectCreateDto.getName() == null || projectCreateDto.getName().isBlank() && projectCreateDto.getName().isEmpty()) {
            throw new DataValidationException("Project name is empty");
        }
        if (projectCreateDto.getDescription() == null || projectCreateDto.getDescription().isBlank() && projectCreateDto.getDescription().isEmpty()) {
            throw new DataValidationException("Project description is empty");
        }
        if (projectCreateDto.getOwnerId() == null) {
            throw new DataValidationException("Project owner id is empty");
        }
    }
}
