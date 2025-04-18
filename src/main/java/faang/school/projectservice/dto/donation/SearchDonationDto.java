package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SearchDonationDto(
        LocalDate creationDate,
        Currency currency,
        @Positive BigDecimal maxAmount,
        @Positive BigDecimal minAmount
) {
}
