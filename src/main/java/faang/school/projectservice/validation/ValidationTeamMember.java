package faang.school.projectservice.validation;

import faang.school.projectservice.dto.member.TeamMemberDto;
import faang.school.projectservice.exception.vacancy.DataValidationException;
import faang.school.projectservice.exception.vacancy.ExceptionMessage;
import faang.school.projectservice.model.TeamRole;
import org.springframework.stereotype.Component;

@Component
public class ValidationTeamMember {
    public void checkMemberRole(TeamMemberDto member) {
        if (!member.getRoles().contains(TeamRole.MANAGER)
                || !member.getRoles().contains(TeamRole.OWNER)) {
            throw new DataValidationException(ExceptionMessage.INAPPROPRIATE_ROLE.getMessage());
        }
    }
}
