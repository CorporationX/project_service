package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.SubProjectDto;
import faang.school.projectservice.dto.project.SubProjectDtoFilter;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;

import java.util.List;


public interface SubProjectService {
    SubProjectDto createSubProject(CreateSubProjectDto createSubProjectDto);

    SubProjectDto updateProject(UpdateSubProjectDto createSubProjectDto);

    List<SubProjectDto> getProjects(SubProjectDtoFilter filter, Long id);

}
