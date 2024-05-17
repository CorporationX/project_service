package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectValidator projectValidator;

    public ProjectDto create(ProjectDto projectDto) {
        projectValidator.checkIsNull(projectDto);
        return projectService.create(projectDto);
    }

    public ProjectDto update(ProjectDto projectDto) {
        projectValidator.checkIsNull(projectDto);
        return projectService.update(projectDto);
    }

    public List<Project> getProjectsByFilter(ProjectFilterDto projectFilterDto) {
        projectValidator.checkIsNull(projectFilterDto);
        return projectService.getProjectsByFilter(projectFilterDto);
    }

    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    public Project getProjectById(Long projectId) {
        projectValidator.checkIsNull(projectId);
        return projectService.getProjectById(projectId);
    }
}