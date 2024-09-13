package faang.school.projectservice.controller.validation;

import faang.school.projectservice.model.TeamRole;
import org.springframework.stereotype.Component;

@Component
public class TeamRoleValidator {
    public void validateIsItTeamRole(TeamRole teamRole) {
        if (teamRole == null) {
            throw new IllegalArgumentException("Team role cannot be null");
        }
        if(!TeamRole.getAll().contains(teamRole)){
            throw new IllegalArgumentException("Invalid team role: " + teamRole);
        }
    }
}
