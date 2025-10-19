package faang.school.projectservice.dto.stageInvitation;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import jakarta.validation.constraints.NotNull;

public record StageInvitationCreateDto(
        @NotNull Long stageId,
        @NotNull Long authorId,
        @NotNull Long invitedId,
        String description
) {
}