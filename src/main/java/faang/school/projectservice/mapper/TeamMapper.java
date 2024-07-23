package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = TeamMemberMapper.class)
public interface TeamMapper {

    TeamDto toDto(Team team);

    List<TeamMemberDto> toDto(List<TeamMember> teams);

    Team toEntity(TeamDto teamDto);

    List<TeamMember> toEntity(List<TeamMemberDto> teams);
}
