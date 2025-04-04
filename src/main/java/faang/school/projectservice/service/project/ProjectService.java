package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectCreateRequestDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateRequestDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.dto.resource.S3ObjectDto;
import faang.school.projectservice.model.Project;

import java.util.List;

public interface ProjectService {

    ProjectResponseDto save(ProjectCreateRequestDto projectDto);

    ProjectResponseDto findById(Long projectId);

    List<ProjectResponseDto> findAll();

    List<ProjectResponseDto> findAllByFilter(ProjectFilterDto filter);

    ProjectResponseDto update(Long id, ProjectUpdateRequestDto projectDto);

    List<Long> getProjectResourceIds(Long projectId);

    Project getProject(Long projectId);

    void createPresentation(long projectId);

    S3ObjectDto downloadPdf(Long projectId);
}
