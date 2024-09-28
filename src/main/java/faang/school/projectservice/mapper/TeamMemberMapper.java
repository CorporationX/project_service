package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMemberMapper {

    TeamMemberDto toTeamMemberDto(TeamMember teamMember);

    TeamMember toTeamMember(TeamMemberDto teamMemberDto);

    List<TeamMemberDto> toTeamMemberDtos(List<TeamMember> teamMembers);
}
