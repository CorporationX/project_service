package faang.school.projectservice.filter.teammember;

import faang.school.projectservice.dto.teammember.TeamMemberFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.TeamMember;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class TeamMemberRoleFilter implements Filter<TeamMemberFilterDto, TeamMember> {

    @Override
    public boolean isApplicable(TeamMemberFilterDto filters) {
        return filters.getRole() != null && !filters.getRole().isEmpty();
    }

    @Override
    public Stream<TeamMember> applyFilter(Stream<TeamMember> entities, TeamMemberFilterDto filters) {
        return entities.filter(member -> member.getRoles().stream()
                .anyMatch(role -> role.name().equalsIgnoreCase(filters.getRole())));
    }
}
