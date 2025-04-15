package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DonationFilterDto(
        Currency currency,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        LocalDate donationDate
) {
}
