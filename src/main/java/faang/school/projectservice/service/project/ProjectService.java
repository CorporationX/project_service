package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;

import java.util.List;

public interface ProjectService {
    ProjectDto create(ProjectDto projectDto);

    ProjectDto update(ProjectDto projectDto);

    List<ProjectDto> getFiltered(ProjectFilterDto filters);

    List<ProjectDto> getAll();

    ProjectDto getById(Long id);
}
