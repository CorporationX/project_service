package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMemberMapper {
    @Mapping(source = "id", target = "teamMemberId")
    @Mapping(source = "team.id", target = "teamId")
    @Mapping(source = "roles", target = "rolesInTeam", qualifiedByName = "rolesToStrings")
    TeamMemberDto toDto(TeamMember entity);

    List<TeamMemberDto> toDtoList(List<TeamMember> teamMembers);

    @Named("rolesToStrings")
    default List<String> rolesToStrings(List<TeamRole> roles) {
        return roles.stream()
                .map(Enum::toString)
                .toList();
    }
}
