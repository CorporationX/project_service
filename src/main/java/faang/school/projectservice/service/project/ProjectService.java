package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;

import java.util.List;

public interface ProjectService {
    ProjectDto createProject(ProjectDto projectDto);

    ProjectDto updateProject(ProjectDto projectDto);

    List<ProjectDto> findAllProjects(ProjectFilterDto filters, Long userId);

    ProjectDto findProjectById(Long id, Long userId);
}
