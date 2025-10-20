package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;

import java.util.List;

public class SubProjectServiceImpl implements SubProjectService {
    @Override
    public SubProjectDto create(long creatorId, CreateSubProjectDto createSubProjectDto) {
        return null;
    }

    @Override
    public SubProjectDto update(long requesterId, UpdateSubProjectDto updateSubProjectDto) {
        return null;
    }

    @Override
    public boolean complete(long subprojectId) {
        return false;
    }

    @Override
    public SubProjectDto getById(long subprojectId) {
        return null;
    }

    @Override
    public List<SubProjectDto> getAllByParentProject(long parentProjectId) {
        return List.of();
    }

    @Override
    public boolean delete(long subprojectId) {
        return false;
    }
}
