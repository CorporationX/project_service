package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.ProjectFilterDto;

import java.util.List;


public interface SubProjectService {

    public ProjectDto createSubProject(SubProjectDto projectId);

    public ProjectDto updateSubProject(long projectId, SubProjectDto subProjectDto);

    public List<ProjectDto> getAllSubProjectsWithFiltr(Long projectId, ProjectFilterDto filtrDto);
}
