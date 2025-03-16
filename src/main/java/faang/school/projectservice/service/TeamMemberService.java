package faang.school.projectservice.service;

import faang.school.projectservice.model.TeamMember;

import java.util.Optional;

public interface TeamMemberService {
    Optional<TeamMember> getTeamMemberById(long teamMemberId);
}
