package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeamMemberMapper {

    @Mapping(source = "team.id", target = "teamId")
    TeamMemberDto teamMemberToTeamMemberDto(TeamMember teamMember);

    @Mapping(target = "stages", ignore = true)
    @Mapping(source = "teamId", target = "team.id")
    TeamMember teamMemberDtoToTeamMember(TeamMemberDto teamMemberDto);

    List<TeamMemberDto> teamMemberListToTeamMemberDtoList(List<TeamMember> teamMembers);
}
