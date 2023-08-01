package faang.school.projectservice.controllers;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.service.ProjectService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/project")
    public ProjectDto createProject(@RequestBody ProjectDto projectDto) {
        validateProject(projectDto);
        return projectService.createProject(projectDto);
    }

    @PutMapping("/project")
    public ProjectDto updateProject(@RequestBody ProjectDto projectDto) {
        validateProject(projectDto);
        return projectService.updateProject(projectDto);
    }

    @GetMapping("project/{id}")
    public ProjectDto getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    @GetMapping("project/filter")
    public List<ProjectDto> getProjectByFilter(@RequestBody ProjectFilterDto filters) {
        return projectService.getProjectByFilter(filters);
    }

    @GetMapping("/project")
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
