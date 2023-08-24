package faang.school.projectservice.dto.invitation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class InvitationEvent {
    Long senderId;
    Long receiverId;
    Long projectId;
    LocalDateTime timestamp;
}
