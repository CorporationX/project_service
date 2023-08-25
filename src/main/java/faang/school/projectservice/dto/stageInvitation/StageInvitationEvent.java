package faang.school.projectservice.dto.stageInvitation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class StageInvitationEvent {
    Long authorId;
    Long invitedId;
    Long projectId;
    LocalDateTime timestamp;
}
