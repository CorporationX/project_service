package faang.school.projectservice.mapper.project.stage;

import faang.school.projectservice.dto.project.stage.StageRoleDto;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageRolesMapper {
    @Mapping(source = "stageId", target = "stage.stageId")
    StageRoles toStageRoles(StageRoleDto stageRoleDto);

    @Mapping(source = "stage.stageId", target = "stageId")
    StageRoleDto toStageRoleDto(StageRoles stageRoles);

    List<StageRoles> toStageRoles(List<StageRoleDto> stageRoleDtos);

    List<StageRoleDto> toStageRoleDtos(List<StageRoles> stageRoles);
}
