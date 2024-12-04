package faang.school.projectservice.dto.stage.invitation;

import lombok.Builder;

@Builder
public record StageInvitationFilterDto(
        Long stageId,
        Long authorId
) {
}
