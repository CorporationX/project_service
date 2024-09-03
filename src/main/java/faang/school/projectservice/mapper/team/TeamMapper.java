package faang.school.projectservice.mapper.team;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = TeamMapperHelper.class)
public interface TeamMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "teamMembers", target = "teamMemberIds", qualifiedByName = "toTeamMemberIds")
    TeamDto toDto(Team entity);

    @Mapping(source = "projectId", target = "project")
    @Mapping(source = "teamMemberIds", target = "teamMembers")
    Team toEntity(TeamDto dto);

    @Named("toTeamMemberIds")
    default List<Long> toTeamMemberIds(List<TeamMember> teamMembers){
        return teamMembers
                .stream()
                .mapToLong(TeamMember::getId)
                .boxed()
                .collect(Collectors.toList());
    }
}
