package faang.school.projectservice.controller;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
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
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping()
    public ProjectDto createProject(@RequestBody ProjectDto projectDto) {
        validateProject(projectDto);
        return projectService.createProject(projectDto);
    }

    @PutMapping()
    public ProjectDto updateProject(@RequestBody ProjectDto projectDto) {
        validateProject(projectDto);
        return projectService.updateProject(projectDto);
    }

    @GetMapping("/{id}")
    public ProjectDto findProjectById(@PathVariable Long id) {
        return projectService.findProjectById(id);
    }

    @GetMapping("/filter")
    public List<ProjectDto> getProjectByFilter(@RequestBody ProjectFilterDto filters) {
        return projectService.getProjectByFilter(filters);
    }

    @GetMapping()
    public List<ProjectDto> getAllProject() {
        return projectService.getAllProject();
    }

    private void validateProject(ProjectDto projectDto) {
        if (projectDto.getName().isBlank() || projectDto.getName() == null) {
            throw new DataValidationException("The name is not valid");
        }
        if (projectDto.getDescription().isBlank() || projectDto.getDescription() == null) {
            throw new DataValidationException("The description is not valid");
        }
        if (projectDto.getOwnerId() < 0) {
            throw new DataValidationException("The ownerId is not valid");
        }
    }
}
