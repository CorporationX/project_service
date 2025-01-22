package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.ProjectRequestDto;
import faang.school.projectservice.dto.ProjectResponseDto;

import java.util.List;

public interface ProjectService {

    ProjectResponseDto save(ProjectRequestDto projectDto);

    ProjectResponseDto findById(Long id);

    List<ProjectResponseDto> findAll();

    List<ProjectResponseDto> findAllByFilter(ProjectFilterDto filter);

    ProjectResponseDto update(Long id, ProjectRequestDto projectDto);
}
