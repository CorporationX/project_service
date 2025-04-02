package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.presentation.PresentationRequestDto;
import faang.school.projectservice.dto.presentation.PresentationFilterDto;
import faang.school.projectservice.dto.presentation.PresentationUpdateDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.dto.resource.S3ObjectDto;
import faang.school.projectservice.model.Project;

import java.util.List;

public interface ProjectService {

    ProjectResponseDto save(PresentationRequestDto projectDto);

    ProjectResponseDto findById(Long projectId);

    List<ProjectResponseDto> findAll();

    List<ProjectResponseDto> findAllByFilter(PresentationFilterDto filter);

    ProjectResponseDto update(Long id, PresentationUpdateDto projectDto);

    List<Long> getProjectResourceIds(Long projectId);

    Project getProject(Long projectId);

    void createPresentation(long projectId);

    S3ObjectDto downloadPdf(Long projectId);
}
