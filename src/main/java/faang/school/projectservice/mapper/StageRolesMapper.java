package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = StageMapper.class)
public interface StageRolesMapper {

//    StageRolesMapper INSTANCE = Mappers.getMapper(StageRolesMapper.class);
    //    @Mapping(target = "teamRole", source = "teamRole", qualifiedByName = "mapTeamRoleToString")
    @Mapping(target = "stageId", source = "stage.stageId")
    StageRolesDto toDto(StageRoles stageRoles);

    //    @Mapping(target = "teamRole", source = "teamRole", qualifiedByName = "mapStringToTeamRole")
    @Mapping(target = "stage.stageId", source = "stageId")
    StageRoles toEntity(StageRolesDto stageRolesDto);

//    @Named("mapTeamRoleToString")
//    default String mapFromRequestStatus(TeamRole role) {
//        return role.toString().toUpperCase();
//    }
//
//    @Named("mapStringToTeamRole")
//    default TeamRole mapToRequestStatus(String role) {
//        return TeamRole.valueOf(role.toUpperCase());
//    }
}
