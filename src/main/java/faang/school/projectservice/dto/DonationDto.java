package faang.school.projectservice.dto;

import faang.school.projectservice.dto.client.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record DonationDto(
        Long id,
        Long userId,
        Long campaignId,
        @NotNull
        @Positive
        BigDecimal amount,
        @NotNull
        Currency currency,
        Long paymentNumber
) {}
