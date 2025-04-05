package faang.school.projectservice.service.teamMember;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;

    public final String TEAM_MEMBER_NOT_FOUND = "Team member not found.";
    public final String TEAM_MEMBER_NOT_INTERN = "Team member is not not an intern in this team.";

    @Transactional
    public void updateInternRole(Long internId, TeamRole newRole) {
        Optional<TeamMember> optionalTeamMember = teamMemberRepository.findById(internId);

        if (optionalTeamMember.isEmpty()){
            throw new EntityNotFoundException(TEAM_MEMBER_NOT_FOUND);
        }

        TeamMember teamMember = optionalTeamMember.get();

        if (!teamMember.getRoles().contains(TeamRole.INTERN)) {
            throw new IllegalArgumentException(TEAM_MEMBER_NOT_INTERN);
        }

        teamMember.getRoles().remove(TeamRole.INTERN);

        if (newRole != null) {
            try {
                teamMember.getRoles().add(newRole);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role: " + newRole);
            }
        }
    }
}
