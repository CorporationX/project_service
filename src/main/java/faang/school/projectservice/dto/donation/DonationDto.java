package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import lombok.Builder;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@FieldNameConstants
@Builder
public record DonationDto(
        Long id,
        BigDecimal amount,
        Long campaignId,
        Currency currency,
        Long userId,
        LocalDateTime donationTime
) {
}