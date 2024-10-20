package faang.school.projectservice.model.dto.donation;

import faang.school.projectservice.model.dto.client.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record DonationDto(
        @NotNull
        Currency currency,
        @NotNull
        @Positive
        BigDecimal amount,
        @NotNull
        Long paymentNumber,
        @NotNull
        Long campaignId,
        @NotNull
        @Positive
        Long userId
) {
}
