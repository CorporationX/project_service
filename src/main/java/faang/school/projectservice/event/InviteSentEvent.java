package faang.school.projectservice.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InviteSentEvent {
    private Long inviterId;
    private Long inviteeId;
    private Long projectId;
    private LocalDateTime createAt;
}
