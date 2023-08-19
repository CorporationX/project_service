package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage_roles.StageRolesDto;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageRolesMapper {

    StageRolesDto toDto(StageRoles stageRoles);

    StageRoles toEntity(StageRolesDto stageRolesDto);

    List<StageRoles> toList(List<StageRolesDto> stageRolesDto);

    List<StageRolesDto> toListDto(List<StageRoles> stageRoles);
}