package faang.school.projectservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InviteSentEvent {
    private Long userId;
    private Long receiverId;
    private Long projectId;
}
