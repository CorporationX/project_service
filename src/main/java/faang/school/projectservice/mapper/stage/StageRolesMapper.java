package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageRolesMapper {

    @Mapping(target = "stage.stageId", source = "stageId")
    StageRoles toStageRoles(StageRolesDto stageRolesDto);

    @Mapping(target = "stageId", source = "stage.stageId")
    StageRolesDto toStageRolesDto(StageRoles stageRoles);
}
