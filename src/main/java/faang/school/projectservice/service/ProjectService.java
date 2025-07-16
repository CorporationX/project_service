package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.project.CreateProjectDto;
import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.dto.client.project.UpdateProjectDto;

import java.util.List;

public interface ProjectService {

    void createProject(CreateProjectDto projectDto);

    void updateProject(long id, UpdateProjectDto projectDto);

    List<ProjectDto> getProjectsFilteredByStatus(ProjectDto projectDto);

    List<ProjectDto> getProjectsFilteredByName();

    List<ProjectDto> getAllProjects();

    ProjectDto getProjectById(long id);
}
