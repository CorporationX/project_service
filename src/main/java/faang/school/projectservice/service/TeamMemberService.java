package faang.school.projectservice.service;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;
    private final TeamMemberJpaRepository teamMemberJpaRepository;

    public TeamMember findById(Long teamMemberId) {
        return teamMemberRepository.findById(teamMemberId);
    }

    public TeamMember findByUserIdAndProjectId(Long userId, Long projectId) {
        return teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId);
    }

    public void deleteTeamMember(TeamMember teamMember) {
        teamMemberJpaRepository.delete(teamMember);
    }

}
