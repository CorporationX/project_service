package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/{projectId}")
    public ProjectDto getProjectById(@PathVariable Long projectId) {
        return projectService.findProjectById(projectId);
    }

    @GetMapping
    public List<ProjectDto> getAllProjects(@RequestBody(required = false) ProjectFilterDto filters) {
        return projectService.findAllProjects(filters);
    }

    @PostMapping("/create")
    public ProjectDto createProject(@RequestBody @Valid CreateProjectDto projectDto) {
        return projectService.createProject(projectDto);
    }

    @PutMapping("/update")
    public ProjectDto updateProject(@RequestBody @Valid UpdateProjectDto projectDto) {
        return projectService.updateProject(projectDto);
    }
}
