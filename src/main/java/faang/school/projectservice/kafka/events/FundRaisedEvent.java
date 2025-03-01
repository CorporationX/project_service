package faang.school.projectservice.kafka.events;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FundRaisedEvent {
    private Long userId;
    private Long projectId;
    private Double amount;
    private LocalDateTime createdAt;
}
