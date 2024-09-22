package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.dto.subproject.request.CreationRequest;
import faang.school.projectservice.dto.subproject.request.UpdatingRequest;

import java.util.List;

public interface SubProjectService {

    SubProjectDto create(CreationRequest creationRequest);

    SubProjectDto update(Long projectId, UpdatingRequest updatingRequest);

    List<SubProjectDto> findSubProjectsByParentId(Long parentId, SubProjectFilterDto filterDto);
}