package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;

import java.util.List;

public interface ProjectService {

    ProjectDto create(ProjectDto projectDto);


    ProjectDto update(long userId, ProjectDto projectDto);

    List<ProjectDto> getFilteredProjects(long userId, ProjectDto dto);
}
