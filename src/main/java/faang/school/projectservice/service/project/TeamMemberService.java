package faang.school.projectservice.service.project;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    public void removeMemberFromTeam(TeamMember teamMember) {
        teamMemberRepository.delete(teamMember);
    }
}