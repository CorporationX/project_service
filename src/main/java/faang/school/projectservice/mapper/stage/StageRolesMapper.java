package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageRolesMapper {

    @Mapping(source = "stageId", target = "stage.stageId")
    StageRoles toEntity(StageRolesDto stageRolesDto);

    @Mapping(source = "stage.stageId", target = "stageId")
    StageRolesDto toDto(StageRoles stageRoles);

    List<StageRolesDto> toListDTO(List<StageRoles> stageRoles);

    List<StageRoles> toList(List<StageRolesDto> stageRolesDto);
}