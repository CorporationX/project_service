package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;

import java.util.List;

public interface ProjectService {

    ProjectDto create(ProjectDto projectDto);

    ProjectDto update(ProjectDto projectDto);

    List<ProjectDto> getAll();

    ProjectDto findById(Long id);

    List<ProjectDto> getAllByFilter(ProjectFilterDto filterDto);

}
