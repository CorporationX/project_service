package faang.school.projectservice.model.events;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FundRaisedEvent {
    private Long userId;
    private Long projectId;
    private BigDecimal amount;
    private LocalDateTime donationTime;
}
