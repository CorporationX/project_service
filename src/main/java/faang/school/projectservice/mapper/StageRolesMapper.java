package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.stage_role.StageRolesCreateRequestDto;
import faang.school.projectservice.dto.stage.stage_role.StageRolesCreateResponseDto;
import faang.school.projectservice.dto.stage.stage_role.StageRolesResponseDto;
import faang.school.projectservice.dto.stage.stage_role.StageRolesUpdateRequestDto;
import faang.school.projectservice.dto.stage.stage_role.StageRolesUpdateResponseDto;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageRolesMapper {

    StageRoles toStageRoles(StageRolesCreateRequestDto stageRolesCreateRequestDto);

    void update(StageRolesUpdateRequestDto stageRolesUpdateRequestDto, @MappingTarget StageRoles stageRoles);

    @Mapping(target = "stageId", source = "stage.stageId")
    StageRolesCreateResponseDto toCreateResponseDto(StageRoles stageRoles);

    @Mapping(target = "stageId", source = "stage.stageId")
    StageRolesUpdateResponseDto toUpdateResponseDto(StageRoles stageRoles);

    @Mapping(target = "stageId", source = "stage.stageId")
    StageRolesResponseDto toResponseDto(StageRoles stageRoles);
}
