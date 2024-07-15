package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;

public interface ProjectService {

    ProjectDto createProject(ProjectDto projectDto);

    ProjectDto updateProject(long id, ProjectUpdateDto projectDto);
}
