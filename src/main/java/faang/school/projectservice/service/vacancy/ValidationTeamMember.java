package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.member.TeamMemberDto;
import faang.school.projectservice.exception.vacancy.DataValidationException;
import faang.school.projectservice.exception.vacancy.ExceptionMessage;
import faang.school.projectservice.model.TeamRole;
import org.springframework.stereotype.Component;

@Component
class ValidationTeamMember {
    public void checkThatTheUserCanCreateAVacancy(TeamMemberDto member) {
        if (!member.getRoles().contains(TeamRole.MANAGER)
                || !member.getRoles().contains(TeamRole.OWNER)) {
            throw new DataValidationException(ExceptionMessage.INAPPROPRIATE_ROLE.getMessage());
        }
    }
}
