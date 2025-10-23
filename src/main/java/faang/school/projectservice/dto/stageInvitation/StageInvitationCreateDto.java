package faang.school.projectservice.dto.stageInvitation;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record StageInvitationCreateDto(
        @NotNull(message = "Stage cannot be empty")
        @Positive(message = "Stage cannot be negative")
        Long stageId,
        @NotNull(message = "Author cannot be empty")
        @Positive(message = "Author cannot be negative")
        Long authorId,
        @NotNull(message = "Invited member cannot be empty")
        @Positive(message = "Invited member cannot be negative")
        Long invitedId,
        @Size(max = 255)
        String description
) {
}