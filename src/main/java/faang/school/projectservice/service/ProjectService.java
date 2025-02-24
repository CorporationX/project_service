package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.ProjectCreateRequestDto;
import faang.school.projectservice.dto.ProjectResponseDto;
import faang.school.projectservice.dto.ProjectUpdateRequestDto;
import faang.school.projectservice.model.Project;

import java.util.List;

public interface ProjectService {

    ProjectResponseDto save(ProjectCreateRequestDto projectDto);

    ProjectResponseDto findById(Long id);

    List<ProjectResponseDto> findAll();

    List<ProjectResponseDto> findAllByFilter(ProjectFilterDto filter);

    ProjectResponseDto update(Long id, ProjectUpdateRequestDto projectDto);

    List<Long> getProjectResourceIds(Long projectId);

    Project getProject(Long projectId);
}
