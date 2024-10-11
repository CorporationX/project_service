package faang.school.projectservice.service;

import faang.school.projectservice.model.dto.project.ProjectDto;
import faang.school.projectservice.model.dto.subproject.SubProjectDto;
import faang.school.projectservice.model.dto.project.ProjectFilterDto;

import java.util.List;


public interface SubProjectService {

     ProjectDto createSubProject(SubProjectDto projectId);

     ProjectDto updateSubProject(long projectId, SubProjectDto subProjectDto);

     List<ProjectDto> getAllSubProjectsWithFilter(Long projectId, ProjectFilterDto filterDto);
}
