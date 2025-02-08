package faang.school.projectservice.dto.donate;

import faang.school.projectservice.dto.client.Currency;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record DonationCreateDto(
        Long userId,
        Long campaignId,
        BigDecimal amount,
        Currency currency,
        LocalDateTime donationTime
) {
}
