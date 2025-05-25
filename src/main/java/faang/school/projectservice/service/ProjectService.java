package faang.school.projectservice.service;

import java.util.List;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;

public interface ProjectService {
    public ProjectDto create(ProjectDto projectDto);
    public ProjectDto update(ProjectDto projectDto);
    public List<ProjectDto> getAll(ProjectFilterDto filter);
    public List<ProjectDto> getAll();
    public ProjectDto getById(long projectId);
    List<ProjectDto> getProjectsByIds(List<Long> ids);
}
