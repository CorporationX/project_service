package faang.school.projectservice.dto.stage_invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Builder
@Component
public class StageInvitationDto {
    private final Long id;
    private final String description;
    private final StageInvitationStatus status;
    private final Long stageId;
    private final Long authorId;
    private final Long invitedId;
}
