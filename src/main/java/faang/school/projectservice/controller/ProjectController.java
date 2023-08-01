package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
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
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ProjectDto createProject(@RequestBody ProjectDto projectDto) {
        validate(projectDto);
        return projectService.createProject(projectDto);
    }

    @PutMapping("/{id}")
    public ProjectDto updateProject(@PathVariable Long id, @RequestBody ProjectDto projectDto) {
        idValidate(id);
        validate(projectDto);
        return projectService.updateProject(id, projectDto);
    }

    @GetMapping("/list")
    public List<ProjectDto> getProjectByFilter(@RequestBody ProjectFilterDto projectFilterDto) {
        return projectService.getProjectByFilter(projectFilterDto);
    }

    @GetMapping("/all")
    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public ProjectDto getProjectById(@PathVariable Long id) {
        idValidate(id);
        return projectService.getProjectById(id);
    }

    private void validate(ProjectDto projectDto) {
        if (projectDto == null) {
            throw new DataValidationException("ProjectDto cannot be null");
        }
        if (projectDto.getName() == null || projectDto.getName().isBlank()) {
            throw new DataValidationException("ProjectDto name cannot be null or empty");
        }
        if (projectDto.getDescription() == null || projectDto.getDescription().isBlank()) {
            throw new DataValidationException("ProjectDto description cannot be null or empty");
        }
    }

    private void idValidate(Long id) {
        if (id == null || id < 0) {
            throw new DataValidationException("Id cannot be negative");
        }
    }
}
