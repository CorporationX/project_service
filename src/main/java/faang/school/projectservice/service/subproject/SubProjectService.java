package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.model.ProjectStatus;

import java.util.List;

public interface SubProjectService {
    SubProjectDto createSubProject(CreateSubProjectDto createSubProjectDto);

    SubProjectDto updateSubProject(UpdateSubProjectDto updateSubProjectDto, Long subProjectId);

    List<SubProjectDto> getSubProjects(Long projectId, String name, ProjectStatus status);
}
