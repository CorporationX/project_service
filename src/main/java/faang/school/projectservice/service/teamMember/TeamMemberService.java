package faang.school.projectservice.service.teamMember;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    public Project getTeamMembersProject(Long teamMemberId) {
        var teamMember = teamMemberRepository.findById(teamMemberId);
        return teamMember.getTeam().getProject();
    }

    public TeamMember getTeamMemberById(Long teamMemberId) {
        return teamMemberRepository.findById(teamMemberId);
    }

    public boolean existsById(Long teamMemberId) {
        return teamMemberRepository.existsById(teamMemberId);
    }
}
