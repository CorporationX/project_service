package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;

import java.util.List;

public interface ProjectService {

    ProjectDto createProject(ProjectDto projectDto);

    ProjectDto updateProject(long id, ProjectUpdateDto projectDto);

    ProjectDto retrieveProject(long id);

    List<ProjectDto> getAllProjects();
}
