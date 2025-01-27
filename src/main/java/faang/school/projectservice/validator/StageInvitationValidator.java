package faang.school.projectservice.validator;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageInvitationValidator {
    private final TeamMemberService teamMemberService;

    public void validateInvitedForCreate(long authorId, long invitedId) {
        TeamMember author = teamMemberService.getTeamMember(authorId);
        TeamMember invited = teamMemberService.getTeamMember(invitedId);

        if (author.getId().equals(invited.getId())) {
            throw new IllegalArgumentException(String.format(
                    "Автор приглашения на этап и приглашенный на этот этап не могут быть одним человеком. " +
                            "\n authorId: %d\n invitedId: %d", authorId, invitedId));
        }
    }

    public void validateStatusPendingCheck(StageInvitation stageInvitation) {
        if (stageInvitation.getStatus().equals(StageInvitationStatus.ACCEPTED)) {
            throw new IllegalArgumentException("The invitation has already been accepted");
        }
        if (stageInvitation.getStatus().equals(StageInvitationStatus.REJECTED)) {
            throw new IllegalArgumentException("The invitation has already been declined");
        }
    }


}
