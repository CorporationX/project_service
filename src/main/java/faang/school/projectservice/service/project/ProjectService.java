package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.filter.project.ProjectFilterDto;

import java.util.List;

public interface ProjectService {

    ProjectDto createProject(ProjectDto dto);

    ProjectDto updateProject(ProjectDto dto);

    List<ProjectDto> getAllProjects(int pageNumber, int pageSize, ProjectFilterDto filters);

    ProjectDto getProjectById(long id);
}
