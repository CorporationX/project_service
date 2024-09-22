package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageRoleMapper {
    StageRolesDto toDto(StageRoles stageRoles);

    List<StageRolesDto> toDtos(List<StageRoles> stageRoles);
}
