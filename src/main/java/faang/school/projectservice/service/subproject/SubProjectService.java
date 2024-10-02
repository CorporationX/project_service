package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;

import java.util.List;


public interface SubProjectService {

     ProjectDto createSubProject(SubProjectDto projectId);

     ProjectDto updateSubProject(long projectId, SubProjectDto subProjectDto);

     List<ProjectDto> getAllSubProjectsWithFilter(Long projectId, ProjectFilterDto filterDto);
}
