package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;

import java.util.List;
import java.util.function.Predicate;

public interface ProjectService {

    ProjectDto create(ProjectDto projectDto);

    ProjectDto update(ProjectDto projectDto);

    List<ProjectDto> getAll();

    ProjectDto findById(long id);

    List<ProjectDto> getAllByFilter(Predicate<ProjectDto> predicate);
}
