package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stagerole.NewStageRolesDto;
import faang.school.projectservice.dto.stagerole.StageRolesDto;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageRolesMapper {

    @Mapping(source = "stage.stageId", target = "stageId")
    StageRolesDto toDto(StageRoles entity);

    @Mapping(target = "stage", ignore = true)
    StageRoles toEntity(StageRolesDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stage", ignore = true)
    StageRoles toEntity(NewStageRolesDto dto);

    List<StageRolesDto> toDtoList(List<StageRoles> entities);

    List<StageRoles> toEntityList(List<NewStageRolesDto> dtoList);

}
