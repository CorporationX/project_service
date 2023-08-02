package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageRolesMapper {

    StageRoles toEntity(StageRolesDto stageRolesDto);

    StageRolesDto toDto(StageRoles stageRoles);

    List<StageRoles> toListDto(List<StageRolesDto> stageRolesDtos);

    List<StageRolesDto> toListEntity(List<StageRoles> stageRoles);
}
