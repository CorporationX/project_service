package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DonationDto(
        Long id,
        Long paymentNumber,
        BigDecimal amount,
        LocalDateTime donationTime,
        Long campaignId,
        Currency currency
) {
}
