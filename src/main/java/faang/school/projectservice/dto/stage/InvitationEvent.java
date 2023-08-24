package faang.school.projectservice.dto.stage;

import lombok.Data;

@Data
public class InvitationEvent {
    Long senderId;
    Long receiverId;
    Long projectId;
}
