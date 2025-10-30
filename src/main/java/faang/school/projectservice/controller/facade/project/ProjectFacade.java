package faang.school.projectservice.controller.facade.project;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ProjectFacade {
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final ResourceMapper resourceMapper;

    public ProjectDto createProject(ProjectCreateDto projectCreateDto) {
        Project project = projectService.createProject(projectCreateDto);

        return projectMapper.toProjectDto(project);
    }

    public ProjectDto updateProject(long projectId, ProjectUpdateDto projectUpdateDto) {
        Project project = projectService.updateProject(projectId, projectUpdateDto);

        return projectMapper.toProjectDto(project);
    }

    public List<ProjectDto> getProjectsByFilter(ProjectFilterDto projectFilterDto) {
        List<Project> projects = projectService.getProjectsByFilter(projectFilterDto);

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

    public ResourceDto addImageCover(long projectId, MultipartFile file) {
        Resource resource = projectService.addImageCover(projectId, file);

        return resourceMapper.toResourceDto(resource);
    }
}
