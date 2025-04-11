package faang.school.projectservice.dto.event;

import java.time.LocalDate;

public record FundRaisedEvent(
        Long userId,
        Long projectId,
        Double raisedAmount,
        LocalDate raiseDate) {
}
