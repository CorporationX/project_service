package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("api/v1/project")
    public void createProject(ProjectDto projectDto) {
        projectService.createProject(projectDto);
    }

    public void updateStatus(ProjectDto projectDto, @NotNull ProjectStatus status) {
        projectService.updateStatus(projectDto, status);
    }

    public void updateDescription(ProjectDto projectDto, @NotBlank String description) {
        projectService.updateDescription(projectDto, description);
    }

    public List<ProjectDto> getProjectsFilters(ProjectFilterDto filterDto, TeamMemberDto teamMemberDto) {
        return projectService.getProjectsFilters(filterDto, teamMemberDto);
    }

    public List<ProjectDto> getProjects() {
        return projectService.getProjects();
    }

    public ProjectDto findById(@NotNull Long id) {
        return projectService.findById(id);
    }
}
