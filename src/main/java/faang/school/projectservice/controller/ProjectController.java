package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/project")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping(value = "/create")
    public void createProject(@RequestBody ProjectDto projectDto) {
        projectService.createProject(projectDto);
    }

    @PutMapping(value = "/update/status/{projectId}")
    public void updateStatus(@PathVariable long projectId, ProjectStatus status) {
        projectService.updateStatus(projectId, status);
    }

    @PutMapping(value = "/update/description/{projectId}")
    public void updateDescription(@PathVariable long projectId, String description) {
        projectService.updateDescription(projectId, description);
    }

    @GetMapping(value = "/filter")
    public List<ProjectDto> getProjectsFilters(ProjectFilterDto filterDto, TeamMemberDto teamMemberDto) {
        return projectService.getProjectsFilters(filterDto, teamMemberDto);
    }

    @GetMapping
    public List<ProjectDto> getProjects() {
        return projectService.getProjects();
    }

    @GetMapping(value = "/{id}")
    public ProjectDto findById(@PathVariable long id) {
        return projectService.findById(id);
    }
}
