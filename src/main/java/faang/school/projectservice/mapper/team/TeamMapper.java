package faang.school.projectservice.mapper.team;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMapper {

    @Mapping(target = "teamMembersId", source = "teamMembers", qualifiedByName = "mapTeamMembers")
    @Mapping(target = "projectId", source = "project.id")
    TeamDto toTeamDto(Team team);

    @Mapping(target = "teamMembers", ignore = true)
    @Mapping(target = "project", ignore = true)
    Team toTeam(TeamDto teamDto);

    List<TeamDto> toTeamDtos(List<Team> teams);

    List<Team> toTeams(List<TeamDto> teamDtos);

    @Named("mapTeamMembers")
    default List<Long> mapTeamMembers(List<TeamMember> teamMembers) {
        return teamMembers != null ? teamMembers.stream()
                .map(TeamMember::getId)
                .collect(Collectors.toList())
                : null;
    }
}
