package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;

import java.util.List;

public interface ProjectService {
    ProjectDto getProjectById(Long id);

    List<ProjectDto> getProjectsByIds(List<Long> ids);
}
