package faang.school.projectservice.service.filter.team;

import faang.school.projectservice.dto.team.TeamMemberFilterDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class MemberRoleFilter implements TeamMemberFilter {

    @Override
    public boolean isAcceptable(TeamMemberFilterDto teamMemberFilterDto) {
        return teamMemberFilterDto.roles() != null && !teamMemberFilterDto.roles().isEmpty();
    }

    @Override
    public Stream<TeamMember> accept(Stream<TeamMember> members, TeamMemberFilterDto filterDto) {
        List<TeamRole> requestedRoles = filterDto.roles();
        return members.filter(member -> member.getRoles().stream()
                .anyMatch(requestedRoles::contains));
    }
}
