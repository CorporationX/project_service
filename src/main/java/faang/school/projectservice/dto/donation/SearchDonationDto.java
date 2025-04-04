package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SearchDonationDto(
        LocalDate creationDate,
        Currency currency,
        @Min(1) BigDecimal maxAmount,
        @Min(1) BigDecimal minAmount
) {
}
