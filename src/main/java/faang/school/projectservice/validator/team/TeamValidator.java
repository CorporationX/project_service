package faang.school.projectservice.validator.team;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.exception.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class TeamValidator {

    public void doesTeamHaveAnUser(TeamDto team, Long userId) {
        boolean userExistsInTheTeam = team.getTeamMembersId().stream()
                .anyMatch((memberId) -> memberId.equals(userId));

        if (!userExistsInTheTeam) {
            throw new AccessDeniedException(String.format("User %s id not belong to the %s team and can't create meet", userId, team.getId()));
        }
    }
}
