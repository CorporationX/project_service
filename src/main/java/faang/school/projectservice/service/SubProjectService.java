package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.SubProjectDtoFilter;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;

import java.util.List;


public interface SubProjectService {
    ProjectDto createSubProject(CreateSubProjectDto createSubProjectDto);
    ProjectDto updateProject(UpdateSubProjectDto createSubProjectDto);
    List<ProjectDto> getProjects(SubProjectDtoFilter filter);

}
