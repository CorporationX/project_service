package faang.school.projectservice.controller.facade.project;

import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectFacade {

    private final ProjectService projectService;

    public ProjectDto create(ProjectDto projectDto, Long ownerId) {
        return projectService.createProject(projectDto, ownerId);
    }

    public ProjectDto update(Long id, ProjectDto projectDto) {
        return projectService.updateProject(id, projectDto);
    }

    public ProjectDto getById(Long id, Long userId) {
        return projectService.getProjectById(id, userId);
    }

    public List<ProjectDto> getAll() {
        return projectService.getAllProjects();
    }

    public List<ProjectDto> getByFilter(String name, ProjectStatus status, Long userId) {
        return projectService.getProjectsByFilter(name, status, userId);
    }
}
