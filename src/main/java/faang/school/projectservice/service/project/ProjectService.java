package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.model.Project;

import java.util.List;

public interface ProjectService {
    ProjectDto create(ProjectCreateDto projectCreateDto);

    void update(ProjectUpdateDto projectUpdateDto, long projectId);

    ProjectDto getById(long projectId);

    List<ProjectDto> getAll();

    List<ProjectDto> getByFilter(ProjectFilterDto projectFilterDto);

    void delete(long projectId);
}