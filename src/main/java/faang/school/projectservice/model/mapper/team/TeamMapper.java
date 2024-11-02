package faang.school.projectservice.model.mapper.team;

import faang.school.projectservice.model.dto.team.TeamDto;
import faang.school.projectservice.model.entity.Candidate;
import faang.school.projectservice.model.entity.Team;
import faang.school.projectservice.model.entity.TeamMember;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMapper {
    @Mappings({
            @Mapping(target = "projectId", source = "project.id"),
            @Mapping(target = "teamMemberIds", source = "teamMembers", qualifiedByName = "teamMemberToId"),
    })
    TeamDto toDto(Team team);

    @Mappings({
            @Mapping(target = "project.id", source = "projectId"),
            @Mapping(target = "teamMembers", source = "teamMemberIds", qualifiedByName = "idToTeamMember"),
    })
    Team toEntity(TeamDto teamDto);

    @Named("teamMemberToId")
    default List<Long> teamMemberToId(List<TeamMember> teamMembers) {
        return teamMembers.stream()
                .map(TeamMember::getId)
                .toList();
    }
    @Named("idToTeamMember")
    default List<TeamMember> idToTeamMember(List<Long> teamMemberIds) {
        return teamMemberIds.stream()
                .map(id -> {
                    TeamMember candidate = new TeamMember();
                    candidate.setId(id);
                    return candidate;
                })
                .toList();
    }
}
