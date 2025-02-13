package faang.school.projectservice.service.filter.team;


import faang.school.projectservice.dto.team.TeamMemberFilterDto;
import faang.school.projectservice.model.TeamMember;

import java.util.stream.Stream;

public interface TeamMemberFilter {
    boolean isAcceptable(TeamMemberFilterDto teamMemberFilterDto);

    Stream<TeamMember> accept(Stream<TeamMember> members, TeamMemberFilterDto teamMemberFilterDto);
}