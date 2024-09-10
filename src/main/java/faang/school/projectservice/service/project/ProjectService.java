package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;

import java.util.List;

public interface ProjectService {
    ProjectDto createProject(CreateProjectDto projectDto);
    ProjectDto updateProject(UpdateProjectDto projectDto);
    List<ProjectDto> findAllProjects(ProjectFilterDto filters);
    ProjectDto findProjectById(Long id);
}
