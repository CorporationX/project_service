package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;

import java.util.List;

public interface ProjectService {
    ProjectDto createSubProject(CreateSubProjectDto createSubProjectDto);

    ProjectDto updateProject(long projectId, UpdateSubProjectDto updateSubProjectDto);

    List<ProjectDto> getFilteredSubProjects(long projectId, ProjectFilterDto projectFilterDto);

}
