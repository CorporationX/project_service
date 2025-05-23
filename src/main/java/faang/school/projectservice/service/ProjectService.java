package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;

import java.util.List;

public interface ProjectService {

    ProjectDto create(ProjectDto projectDto);

    ProjectDto update(ProjectDto projectDto);

    List<ProjectDto> getFilteredProjects(ProjectFilterDto dto);

    List<ProjectDto> getAllProjects(long userId);

    ProjectDto getProjectById(long userId, long projectId);
}
