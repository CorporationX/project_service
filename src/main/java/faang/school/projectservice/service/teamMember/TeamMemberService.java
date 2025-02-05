package faang.school.projectservice.service.teamMember;

import faang.school.projectservice.exception.NoSuchTeamMemberException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    public TeamMember getTeamMemberByIdAndProjectId(Long teamMemberId, Long projectId) {
        TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(teamMemberId, projectId);

        if (teamMember == null) {
            throw new NoSuchTeamMemberException("No team member found with id %d in project %d"
                    .formatted(teamMemberId, projectId));
        }

        return teamMember;
    }
}
