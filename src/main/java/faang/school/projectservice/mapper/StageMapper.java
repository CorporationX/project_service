package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {
    StageRolesMapper stageRolesMapper = Mappers.getMapper(StageRolesMapper.class);
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "stageRolesDto", source = "stageRoles", qualifiedByName = "mapListRolesToListRolesDto")
    StageDto toDto(Stage stage);

    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "stageRoles", source = "stageRolesDto", qualifiedByName = "mapListRolesDtoToListRoles")
    Stage toEntity(StageDto stageDto);

    @Named("mapListRolesDtoToListRoles")
    default List<StageRoles> toList(List<StageRolesDto> stageRolesDtos) {
        return stageRolesDtos.stream().map(stageRolesMapper::toEntity).toList();
    }

    @Named("mapListRolesToListRolesDto")
    default List<StageRolesDto> toListDto(List<StageRoles> stageRoles) {
        return stageRoles.stream().map(stageRolesMapper::toDto).toList();
    }
}