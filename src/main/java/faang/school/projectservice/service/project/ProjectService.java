package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.ProjectCreateRequestDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.ProjectResponseDto;
import faang.school.projectservice.dto.ProjectUpdateRequestDto;
import faang.school.projectservice.dto.resource.S3ObjectDto;
import faang.school.projectservice.model.Project;
import org.springframework.core.io.InputStreamResource;

import java.util.List;


public interface ProjectService {

    ProjectResponseDto save(ProjectCreateRequestDto projectDto);

    ProjectResponseDto findById(Long id);

    List<ProjectResponseDto> findAll();

    List<ProjectResponseDto> findAllByFilter(ProjectFilterDto filter);

    ProjectResponseDto update(Long id, ProjectUpdateRequestDto projectDto);

    List<Long> getProjectResourceIds(Long projectId);

    Project getProject(Long projectId);

    void createPresentation(long projectId);

    S3ObjectDto downloadPdf(Long projectId);

    InputStreamResource getPresentation(S3ObjectDto obj);
}
