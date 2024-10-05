package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMemberMapper {

    @Mapping(source = "id", target = "teamMemberId")
    @Mapping(source = "team.id", target = "teamId")
    @Mapping(source = "roles", target = "teamRoles")
    TeamMemberDto toDto(TeamMember entity);


    TeamMember toEntity(TeamMemberDto dto);

    List<TeamMemberDto> toDtoList(List<TeamMember> teamMembers);

    default List<String> roles(TeamMember teamMember) {
        return teamMember.getRoles().stream()
                .map(Enum::toString)
                .toList();
    }
}
