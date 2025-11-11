package faang.school.projectservice.service.project;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;

    public void removeMemberFromTeam(TeamMember teamMember) {
        validationTeamMemberIsNotEmpty(teamMember);
        teamMemberRepository.delete(teamMember);
    }

    private void validationTeamMemberIsNotEmpty(TeamMember teamMember) {
        if (teamMember == null) {
            throw new EntityNotFoundException("Team member can't be empty");
        }
    }
}