package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamMemberValidator {

    public void validateTeamMember(TeamMember teamMember) {
        if (!teamMember.getRoles().contains(TeamRole.OWNER)) {
            throw new DataValidationException("Team member with Id " + teamMember.getId() + " is not " + TeamRole.OWNER);
        }
    }
}