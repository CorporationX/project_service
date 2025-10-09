package faang.school.projectservice.dto.stageInvitation;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;

public record StageInvitationDto(
        String description,
        StageInvitationStatus status,
        Stage stage,
        TeamMember invited
) {
}