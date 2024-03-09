package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface ProjectService {
    ProjectDto createSubProject(CreateSubProjectDto createSubProjectDto);

    ProjectDto create(ProjectDto projectDto);

    ProjectDto update(ProjectDto projectDto);

    List<ProjectDto> getAll();

    ProjectDto findById(Long id);

    List<ProjectDto> getAllByFilter(ProjectFilterDto filterDto);

    ProjectDto updateSubProject(long projectId, UpdateSubProjectDto updateSubProjectDto);

    List<ProjectDto> getFilteredSubProjects(long projectId, ProjectFilterDto projectFilterDto);

}
