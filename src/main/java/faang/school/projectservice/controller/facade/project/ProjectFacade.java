package faang.school.projectservice.controller.facade.project;

import faang.school.projectservice.dto.client.project.ProjectCreateDto;
import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.dto.client.project.ProjectUpdateDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectFacade {

    private final ProjectService projectService;

    public ProjectDto create(ProjectCreateDto projectCreateDto, Long ownerId) {
        Project project = projectService.create(projectCreateDto, ownerId);
        return ProjectMapper.toDto(project);
    }

    public ProjectDto update(ProjectUpdateDto projectUpdateDto, Long id, Long ownerId) {
        Project project = projectService.update(id, projectUpdateDto, ownerId);
        return ProjectMapper.toDto(project);
    }

    public ProjectDto getById(Long id, Long userId) {
        Project project = projectService.getProjectById(id, userId);
        return ProjectMapper.toDto(project);
    }

    public List<ProjectDto> getAll() {
        return projectService.getAllProjects();
    }

    public List<ProjectDto> getByFilter(String name, ProjectStatus status, Long userId) {
        return projectService.getProjectsByFilter(name, status, userId);
    }
}
