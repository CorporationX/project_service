package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StageRolesMapper {
    @Mapping(target = "stage", ignore = true)
    StageRoles toEntity(StageRolesDto stageRoleDto);

    StageRolesDto toDto(StageRoles stageRole);
}
