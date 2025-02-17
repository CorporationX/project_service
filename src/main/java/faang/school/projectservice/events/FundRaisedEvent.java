package faang.school.projectservice.events;

import lombok.Data;
import scala.math.BigDecimal;

import java.sql.Timestamp;

@Data
public class FundRaisedEvent {
    private Long userId;
    private Long projectId;
    private BigDecimal amount;
    private Timestamp donationTime;
}
