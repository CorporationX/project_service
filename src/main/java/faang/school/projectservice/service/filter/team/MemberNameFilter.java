package faang.school.projectservice.service.filter.team;

import faang.school.projectservice.dto.team.TeamMemberFilterDto;
import faang.school.projectservice.model.TeamMember;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MemberNameFilter implements TeamMemberFilter {

    @Override
    public boolean isAcceptable(TeamMemberFilterDto teamMemberFilterDto) {
        return teamMemberFilterDto.name() != null;
    }

    @Override
    public Stream<TeamMember> accept(Stream<TeamMember> members, TeamMemberFilterDto donationFilterDto) {
        return members.filter(member -> matchesPattern(donationFilterDto.name(), member.getNickname()));
    }

    private boolean matchesPattern(String pattern, String value) {
        return pattern == null || value.matches(pattern);
    }
}
