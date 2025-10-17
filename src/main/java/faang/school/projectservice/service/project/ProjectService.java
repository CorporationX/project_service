package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;

import java.util.List;

public interface ProjectService {
    ProjectDto create(ProjectCreateDto projectCreateDto);

    ProjectDto update(ProjectUpdateDto projectUpdateDto, long projectId);

    ProjectDto getById(long projectId);

    List<ProjectDto> getByFilter(ProjectFilterDto projectFilterDto);

    void delete(long projectId);
}