package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;

import java.util.List;

public interface ProjectService {

    ProjectDto create(CreateProjectDto createProjectDto);

    ProjectDto update(long projectId, UpdateProjectDto updateProjectDto);

    List<ProjectDto> getAllProjects();

    ProjectDto getProjectById(long projectId);

    List<ProjectDto> getByFilters(ProjectFilterDto projectFilterDto);
}