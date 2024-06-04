package faang.school.projectservice.validation.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationCreateDto;
import faang.school.projectservice.exceptions.NoAccessException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.validation.stage.StageValidator;
import faang.school.projectservice.validation.team_member.TeamMemberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageInvitationValidatorImpl implements StageInvitationValidator {

    private final TeamMemberValidator teamMemberValidator;
    private final StageValidator stageValidator;

    @Override
    public void validateUserInvitePermission(TeamMember user, StageInvitation invitation) {
        if (!invitation.getInvited().equals(user)) {
            throw new NoAccessException("User with id=" + user.getId() + " does not have access to invite with id=" + invitation.getId());
        }
    }

    @Override
    public void validateExistences(StageInvitationCreateDto stageInvitationCreateDto) {
        teamMemberValidator.validateExistence(stageInvitationCreateDto.getAuthorId());
        teamMemberValidator.validateExistence(stageInvitationCreateDto.getInvitedId());
        stageValidator.validateExistence(stageInvitationCreateDto.getStageId());
    }
}
