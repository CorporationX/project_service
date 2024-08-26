package faang.school.projectservice.validator;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TeamValidator {

    public boolean verifyUserExistenceInTeam(long userId, Team team) {
        return team.getTeamMembers().stream()
                .map(TeamMember::getUserId)
                .anyMatch(teamMemberId -> teamMemberId == userId);
    }
}
