package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;

import java.util.List;

public interface ProjectService {

    ProjectDto create(ProjectDto projectDto);

    ProjectDto update(ProjectDto projectDto);

    List<ProjectDto> getFilteredProjects(long userId, ProjectFilterDto dto);

    ProjectDto getProjectById(long userId, long projectId);
}
