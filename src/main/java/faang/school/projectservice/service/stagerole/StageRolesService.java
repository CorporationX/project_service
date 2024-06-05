package faang.school.projectservice.service.stagerole;

import faang.school.projectservice.dto.stagerole.StageRolesDto;

import java.util.List;

public interface StageRolesService {
    StageRolesDto create(StageRolesDto dto);

    List<StageRolesDto> getAllByStageId(long stageId);

    StageRolesDto getById(long stageRoleId);

    void delete(long stageRoleId);
}
