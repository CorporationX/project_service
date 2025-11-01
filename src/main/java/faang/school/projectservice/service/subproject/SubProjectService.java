package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;

import java.util.List;

public interface SubProjectService {
    SubProjectDto create(long creatorId, CreateSubProjectDto createSubProjectDto);

    SubProjectDto update(long requesterId, long subProjectId, UpdateSubProjectDto updateSubProjectDto);

    boolean complete(long requesterId, long subprojectId);

    SubProjectDto getById(long subprojectId);

    List<SubProjectDto> getAllByParentProject(long parentProjectId);

    boolean delete(long requesterId, long subprojectId);
}
