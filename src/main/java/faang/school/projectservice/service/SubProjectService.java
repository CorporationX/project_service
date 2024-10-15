package faang.school.projectservice.service;

import faang.school.projectservice.model.dto.ProjectDto;

import java.util.List;

public interface SubProjectService {
    ProjectDto create(ProjectDto projectDto);

    List<ProjectDto> getFilteredSubProjects(ProjectDto projectDto);

    ProjectDto updatingSubProject(ProjectDto projectDto);
}
