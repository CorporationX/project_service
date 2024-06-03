package faang.school.projectservice.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InviteSentEvent {
    private Long userId;
    private Long receiverId;
    private Long projectId;
}
