package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {
    private final ProjectService projectService;

    public void createProject(ProjectDto projectDto) {
        projectService.createProject(projectDto);
    }

    public void updateStatus(ProjectDto projectDto, ProjectStatus status) {
        projectService.updateStatus(projectDto, status);
    }

    public void updateDescription(ProjectDto projectDto, String description) {
        projectService.updateDescription(projectDto, description);
    }

    public List<ProjectDto> getProjectsFilters(ProjectFilterDto filterDto, TeamMemberDto teamMemberDto) {
        return projectService.getProjectsFilters(filterDto, teamMemberDto);
    }

    public List<ProjectDto> getProjects() {
        return projectService.getProjects();
    }

    public ProjectDto findById(long id) {
        return projectService.findById(id);
    }
}
