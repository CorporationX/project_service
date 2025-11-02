package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.With;

import java.math.BigDecimal;

@With
@Builder
public record CreateDonationDto(
        @NotNull(message = "Amount should be present")
        BigDecimal amount,

        @NotNull(message = "Campaign id should be present")
        Long campaignId,

        @NotNull(message = "Payment currency should be present")
        Currency paymentCurrency,

        Currency targetCurrency
) {
}