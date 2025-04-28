package faang.school.projectservice.dto.event;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record FundRaisedEvent(
        Long userId,
        Long projectId,
        BigDecimal raisedAmount,
        LocalDate raiseDate) {
}

