package faang.school.projectservice.service.teamfilter;

import faang.school.projectservice.dto.team.TeamFilterDto;
import faang.school.projectservice.model.TeamMember;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class TeamMemberRoleFilter implements TeamMemberFilter{

    @Override
    public boolean isApplicable(TeamFilterDto teamFilterDto) {
        return teamFilterDto != null;
    }

    @Override
    public Stream<TeamMember> apply(Stream<TeamMember> teamMembers, TeamFilterDto teamFilterDto) {
        return teamMembers.filter(teamMember -> teamMember.getRoles().contains(teamFilterDto.getTeamRole()));
    }
}
