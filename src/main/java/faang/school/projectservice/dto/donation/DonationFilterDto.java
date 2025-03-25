package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record DonationFilterDto(
        LocalDateTime fromDonationTime,
        LocalDateTime toDonationTime,
        Currency currency,
        BigDecimal minAmount,
        BigDecimal maxAmount
) {
}
