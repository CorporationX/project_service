package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.ProjectFilterDto;

import java.util.List;


public interface SubProjectService {

     ProjectDto createSubProject(SubProjectDto projectId);

     ProjectDto updateSubProject(long projectId, SubProjectDto subProjectDto);

     List<ProjectDto> getAllSubProjectsWithFiltr(Long projectId, ProjectFilterDto filtrDto);
}
