package faang.school.projectservice.service.project;



import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.model.ProjectStatus;

import java.util.List;

public interface ProjectService {
    ProjectDto create(long requesterId, CreateProjectDto createDto);

    ProjectDto update(long requesterId, long projectId, UpdateProjectDto updateDto);

    ProjectDto getById(long requesterId, long projectId);

    List<ProjectDto> getAll(long requesterId);

    List<ProjectDto> search(long requesterId, String name, ProjectStatus status);
}
