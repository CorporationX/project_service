package faang.school.projectservice.service.pdf;


import faang.school.projectservice.dto.project.ProjectPresentationDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.ProjectCreateRequestDto;
import faang.school.projectservice.dto.ProjectResponseDto;
import faang.school.projectservice.dto.ProjectUpdateRequestDto;
import faang.school.projectservice.model.Project;

import java.io.InputStream;
import java.util.List;


public interface ProjectPdfService {

    ProjectResponseDto save(ProjectCreateRequestDto projectDto);

    ProjectResponseDto findById(Long id);

    List<ProjectResponseDto> findAll();

    List<ProjectResponseDto> findAllByFilter(ProjectFilterDto filter);

    ProjectResponseDto update(Long id, ProjectUpdateRequestDto projectDto);

    List<Long> getProjectResourceIds(Long projectId);

    Project getProject(Long projectId);

    InputStream createProjectPresentation(ProjectPresentationDto dto);
}
