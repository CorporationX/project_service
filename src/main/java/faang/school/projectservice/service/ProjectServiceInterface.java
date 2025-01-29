package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;

import java.util.List;

public interface ProjectServiceInterface {
    ProjectDto createProject(ProjectDto projectDto);

    ProjectDto updatedProject(ProjectDto projectDto);

    List<ProjectDto> getAllAvailableProjectsForUserWithFilter(ProjectFilterDto filterDto, long userId);

    List<ProjectDto> getAllAvailableProjectsForUser(long userId);

    ProjectDto getProjectById(long projectId);
}
