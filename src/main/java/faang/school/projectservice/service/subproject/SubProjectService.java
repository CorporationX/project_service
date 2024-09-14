package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectFilterDto;

import java.util.List;

public interface SubProjectService {

    SubProjectDto create(SubProjectDto subProjectDto);

    SubProjectDto update(SubProjectDto subProjectDto);

    List<SubProjectDto> findSubProjectsByParentId(Long parentId, SubProjectFilterDto filterDto);
}