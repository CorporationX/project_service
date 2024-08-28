package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.meet.Meet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    @Mapping(source = "teamMembers", target = "teamMembersId", qualifiedByName = "teamMemberToIds")
    @Mapping(source = "teamMeets", target = "teamMeetsId", qualifiedByName = "teamMeetsToIds")
    @Mapping(source = "project.id", target = "projectId")
    TeamDto toDto(Team team);

    @Named("teamMemberToIds")
    default List<Long> mapTeamMembersToIdList(List<TeamMember> teamMembers) {
        return teamMembers.stream().map(TeamMember::getId).toList();
    }

    @Named("teamMeetsToIds")
    default List<Long> mapTeamMeetsToIdList(List<Meet> teamMeets) {
        return teamMeets.stream().map(Meet::getId).toList();
    }
}
