package faang.school.projectservice.mapper.teammember;

import faang.school.projectservice.dto.member.TeamMemberDto;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMemberMapper {

    @Mapping(target = "team", ignore = true)
    TeamMember toEntity(TeamMemberDto teamMemberDto);

    @Mapping(source = "team", target = "teamId", qualifiedByName = "teamMapToTeamId")
    TeamMemberDto toDto(TeamMember teamMember);

    @Named("teamMapToTeamId")
    private Long teamMapToTeamId(Team team) {
        return team.getId();
    }
}
