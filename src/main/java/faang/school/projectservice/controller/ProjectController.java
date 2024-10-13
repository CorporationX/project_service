package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/project")
@RequiredArgsConstructor
@Validated
public class ProjectController {

    private final ProjectService projectService;
    private final UserContext userContext;

    @PostMapping
    public void createProject(@RequestBody ProjectDto projectDto) {
        projectService.createProject(projectDto);
    }

    @PutMapping("/status")
    public void updateStatus(@RequestBody ProjectDto projectDto, @RequestParam ProjectStatus status) {
        projectService.updateStatus(projectDto, status);
    }

    @PutMapping("/description")
    public void updateDescription(@RequestBody ProjectDto projectDto, @NotBlank @RequestParam String description) {
        projectService.updateDescription(projectDto, description);
    }

    @PostMapping("/filter")
    public List<ProjectDto> getProjectsFilters(@RequestBody ProjectFilterDto filterDto, @RequestBody TeamMemberDto teamMemberDto) {
        return projectService.getProjectsFilters(filterDto, teamMemberDto);
    }

    @GetMapping
    public List<ProjectDto> getProjects() {
        return projectService.getProjects();
    }

    @GetMapping("/{id}")
    public ProjectDto findById(@Positive @PathVariable Long id) {
        return projectService.findById(id, userContext.getUserId());
    }
}

