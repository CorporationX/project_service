package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.ProjectFilterDto;

import java.util.List;


public interface SubProjectService {

    public SubProjectDto createSubProject(Long projectId);

    public SubProjectDto updateSubProject(SubProjectDto subProjectDto);

    public List<SubProjectDto> getAllSubProjectsWithFiltr(Long projectId, ProjectFilterDto filtrDto);
}
