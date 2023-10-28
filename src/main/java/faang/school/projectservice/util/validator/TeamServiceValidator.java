package faang.school.projectservice.util.validator;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.exception.enumException.team.TeamRoleEnumException;
import org.springframework.stereotype.Component;

@Component
public class TeamServiceValidator {

    public void validateTeamRole(TeamMember teamMember) {
        if (!teamMember.getRoles().contains(TeamRole.TEAMLEAD) && !teamMember.getRoles().contains(TeamRole.OWNER)) {
            throw new TeamRoleEnumException("...");
        }
    }

//    public void validateOwnerProject()
}
