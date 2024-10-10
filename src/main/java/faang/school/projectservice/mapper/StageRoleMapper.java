package faang.school.projectservice.mapper;

import faang.school.projectservice.model.dto.StageRoleDto;
import faang.school.projectservice.model.entity.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageRoleMapper {
    @Mapping(source = "stage.stageId", target = "stageId")
    StageRoleDto toDto(StageRoles stageRoles);

    List<StageRoleDto> toDtoList(List<StageRoles> stageRolesList);

    @Mapping(source = "stageId", target = "stage.stageId")
    StageRoles toEntity(StageRoleDto stageRolesDto);

    List<StageRoles> toEntityList(List<StageRoleDto> stageRolesDtoList);
}
