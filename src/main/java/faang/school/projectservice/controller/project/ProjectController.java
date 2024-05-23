package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectValidator projectValidator;

    @PostMapping("/create")
    public ProjectDto create(@RequestBody ProjectDto projectDto) {
        projectValidator.checkIsNull(projectDto);
        return projectService.create(projectDto);
    }

    @PutMapping("/update")
    public ProjectDto update(@RequestBody ProjectDto projectDto) {
        projectValidator.checkIsNull(projectDto);
        return projectService.update(projectDto);
    }

    @PostMapping("/filter")
    public List<Project> getProjectsByFilter(@RequestBody ProjectFilterDto projectFilterDto) {
        projectValidator.checkIsNull(projectFilterDto);
        return projectService.getProjectsByFilter(projectFilterDto);
    }

    @GetMapping()
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public Project getProjectById(@PathVariable("id") Long projectId) {
        projectValidator.checkIsNull(projectId);
        return projectService.getProjectById(projectId);
    }
}