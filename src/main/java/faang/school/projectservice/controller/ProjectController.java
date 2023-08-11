package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/")
    public ProjectDto create(@RequestBody ProjectDto projectDto) {
        return projectService.create(projectDto);
    }

    @PutMapping("/")
    public ProjectDto changeStatus(@RequestBody ProjectDto projectDto, Long id) {
        return projectService.update(projectDto, id);
    }

    @PostMapping("/{userId}/get-by-filters")
    public List<ProjectDto> getByFilters(@RequestBody ProjectFilterDto projectFilterDto, @PathVariable long userId) {
        return projectService.getByFilters(projectFilterDto, userId);
    }

    @GetMapping("/all")
    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProject();
    }

    @DeleteMapping("/{userId}")
    public void deleteProjectById(@PathVariable long id) {
        projectService.deleteProjectById(id);
    }

    @GetMapping("/{userId}")
    public ProjectDto getProjectById(@PathVariable long userId) {
        return projectService.getProjectById(userId);
    }

    public ProjectDto create(ProjectDto projectDto) {
        return projectService.create(projectDto);
    }

    public ProjectDto changeStatus(ProjectDto projectDto, Long id) {
        if (id == null) {
            throw new DataValidationException("Project id is null");
        }
        return projectService.updateStatusAndDescription(projectDto, id);
    }
}
