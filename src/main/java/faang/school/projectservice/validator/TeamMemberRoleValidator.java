package faang.school.projectservice.validator;


import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamMemberRoleValidator {
    private final TeamMemberRepository teamMemberRepository;

    public boolean isTeamManager(Long userID) {
     return teamMemberRepository.findByUserId(userID).get(0).getRoles().contains(TeamRole.MANAGER);
    }
}
