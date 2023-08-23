package faang.school.projectservice.publisher.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class InviteSentEvent {
    private Long authorId;
    private Long invitedId;
    private Long projectId;
}
