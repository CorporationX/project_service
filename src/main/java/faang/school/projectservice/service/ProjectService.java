package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.FilterSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;

import java.util.List;

public interface ProjectService {
    ProjectDto createSubProject(long ownerId, CreateSubProjectDto createSubProjectDto);

    ProjectDto updateSubProject(long userId, UpdateSubProjectDto updateSubProjectDto);

    List<ProjectDto> getSubProjects(Long projectId, FilterSubProjectDto filter, Integer size, Integer from);
}