package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Validated
public class ProjectController {
    private final ProjectService projectService;

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
