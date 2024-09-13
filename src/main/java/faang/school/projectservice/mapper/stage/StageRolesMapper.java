package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface StageRolesMapper {

    StageRolesDto toDto(StageRoles stage);

    StageRoles toEntity(StageRolesDto stageRolesDto);
}
