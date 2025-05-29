package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectForUpdateDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectForCreationDto;
import faang.school.projectservice.dto.project.ProjectOutputDto;

import java.util.List;

public interface ProjectService {

    ProjectOutputDto create(ProjectForCreationDto projectDto);

    ProjectOutputDto update(ProjectForUpdateDto projectDto);

    List<ProjectOutputDto> getFilteredProjects(ProjectFilterDto dto);

    ProjectOutputDto getProjectById(long projectId);
}
