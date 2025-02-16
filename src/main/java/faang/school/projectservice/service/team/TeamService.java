package faang.school.projectservice.service.team;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.model.TeamMember;

import java.util.Optional;

public interface TeamService {

    TeamDto createTeam(Long authorId, TeamDto teamDto);

    void deleteMemberByUserId(Long userId);

    Optional<TeamMember> findMemberByUserIdAndProjectId(Long teamMemberId, Long projectId);
}
