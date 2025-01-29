package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.ProjectStatus;

import java.util.List;

public interface ProjectService {
    ProjectDto createProject(ProjectDto projectDto);
    ProjectDto updateProject(Long id, ProjectDto projectDto);
    List<ProjectDto> getAllProjects(String name, ProjectStatus status, Boolean isPrivate, Long userId);
    ProjectDto getProjectById(Long id);
}
