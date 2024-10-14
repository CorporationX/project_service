package faang.school.projectservice.filter.teammember;

import faang.school.projectservice.dto.teammember.TeamMemberFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.TeamMember;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class TeamMemberTeamNameFilter implements Filter<TeamMemberFilterDto, TeamMember> {

    @Override
    public boolean isApplicable(TeamMemberFilterDto filters) {
        return filters.getProjectName() != null && !filters.getProjectName().isEmpty();
    }

    @Override
    public Stream<TeamMember> applyFilter(Stream<TeamMember> entities, TeamMemberFilterDto filters) {
        return entities.filter(member -> member.getTeam().getProject().getName().equalsIgnoreCase(filters.getProjectName()));
    }
}
