package faang.school.projectservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InviteSentEvent {
    private Long inviterId;
    private Long inviteeId;
    private Long projectId;
    private LocalDateTime createAt;
}
