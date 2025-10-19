package faang.school.projectservice.dto.stageInvitation;

import jakarta.validation.constraints.NotNull;

public record StageInvitationAcceptDto(
        @NotNull Long idInvitation,
        @NotNull Long idInvited
) {
}