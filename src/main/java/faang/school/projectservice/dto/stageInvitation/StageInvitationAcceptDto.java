package faang.school.projectservice.dto.stageInvitation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StageInvitationAcceptDto(
        @NotNull(message = "Specify invitation!")
        @Positive(message = "Invitation cannot be negative")
        Long idInvitation,
        @NotNull(message = "Specify invited member!")
        @Positive(message = "Invited member cannot be negative")
        Long idInvited
) {
}