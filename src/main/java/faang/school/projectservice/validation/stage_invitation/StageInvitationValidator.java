package faang.school.projectservice.validation.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationCreateDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

public interface StageInvitationValidator {

    void validateUserInvitePermission(TeamMember user, StageInvitation invitation);

    void validateExistences(StageInvitationCreateDto stageInvitationCreateDto);
}
