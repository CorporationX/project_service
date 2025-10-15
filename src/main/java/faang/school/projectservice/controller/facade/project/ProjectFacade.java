package faang.school.projectservice.controller.facade.project;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ProjectFacade {
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;

    public ProjectDto createProject(ProjectCreateDto projectCreateDto) {
        Project project = projectService.createProject(projectCreateDto);

        return projectMapper.toProjectDto(project);
    }

    public ProjectDto updateProject(long projectId, ProjectUpdateDto projectUpdateDto) {
        Project project = projectService.updateProject(projectId, projectUpdateDto);

        return projectMapper.toProjectDto(project);
    }

    public List<ProjectDto> getProjectsByFilter(ProjectFilterDto projectFilterDto, long userId) {
        List<Project> projects = projectService.getProjectsByFilter(projectFilterDto, userId);

        return projects.stream()
                .map(projectMapper::toProjectDto)
                .toList();
    }

    public List<ProjectDto> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();

        return projects.stream()
                .map(projectMapper::toProjectDto)
                .toList();
    }

    public ProjectDto getProjectById(long projectId) {
        Project project = projectService.getProjectById(projectId);

        return projectMapper.toProjectDto(project);

    }

    public void deleteProject(long projectId) {
        projectService.deleteProject(projectId);
    }
}
