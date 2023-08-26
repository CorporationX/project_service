package faang.school.projectservice.dto.stageInvitation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class StageInvitationEvent {
    private Long authorId;
    private Long invitedId;
    private Long projectId;
    private LocalDateTime timestamp;
}
