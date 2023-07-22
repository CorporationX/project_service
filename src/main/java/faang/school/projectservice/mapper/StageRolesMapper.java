package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageRolesMapper {

    @Mapping(target = "stageId", source = "stage.stageId")
    StageRolesDto toDto(StageRoles stageRoles);

    @Mapping(target = "stage.stageId", source = "stageId")
    StageRoles toEntity(StageRolesDto stageRolesDto);
}