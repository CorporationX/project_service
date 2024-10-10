package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMapper {
    @Mapping(source = "id", target = "teamId")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "teamMembers", target = "teamMemberIds", qualifiedByName = "toTeamMemberIds")
    TeamDto toDto(Team team);

    @Named("toTeamMemberIds")
    default List<Long> toTeamMemberIds(List<TeamMember> teamMembers) {
        return Objects.nonNull(teamMembers) ?
                teamMembers.stream()
                .map(TeamMember::getId)
                .toList() : List.of();
    }
}
