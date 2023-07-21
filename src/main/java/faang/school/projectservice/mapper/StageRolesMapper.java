package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageRolesMapper {
    @Mapping(target = "stageId", source = "stage.id")
    @Mapping(target = "role", source = "teamRole.role", qualifiedByName = "mapTeamRoleToString")
    StageRolesDto toDto(StageRoles stageRoles);

    @Mapping(target = "stage.id", source = "stageId")
    @Mapping(target = "teamRole.role", source = "role", qualifiedByName = "mapStringToTeamRole")
    StageRoles toEntity(StageRolesDto stageRolesDto);

    @Named("mapTeamRoleToString")
    default String mapFromRequestStatus(TeamRole role) {
        return role.toString().toUpperCase();
    }

    @Named("mapStringToTeamRole")
    default TeamRole mapToRequestStatus(String role) {
        return TeamRole.valueOf(role.toUpperCase());

    }
}
