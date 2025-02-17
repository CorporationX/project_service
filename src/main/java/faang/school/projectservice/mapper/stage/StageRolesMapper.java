package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface StageRolesMapper {
    StageRoles toEntity(StageRolesDto stageRolesDto);

    StageRolesDto toStageRolesDto(StageRoles stageRoles);

    void updateStageRoles(StageRolesDto stageRolesDto, @MappingTarget StageRoles stageRoles);
}