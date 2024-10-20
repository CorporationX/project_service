package faang.school.projectservice.model.dto.donation;

import faang.school.projectservice.model.dto.client.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DonationFilterDto(
        LocalDateTime creationDate,
        Currency currency,
        BigDecimal minAmount,
        BigDecimal maxAmount
) {
}
