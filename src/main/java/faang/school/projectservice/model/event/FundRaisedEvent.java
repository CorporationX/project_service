package faang.school.projectservice.model.event;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FundRaisedEvent {
    private Long projectId;
    private Long actorId;
    private Long amount;
    private LocalDateTime receivedAt;
}