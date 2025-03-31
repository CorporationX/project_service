package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record DonationDto(

        Long paymentNumber,

        @Min(value = 1, message = "Amount sum must be greater {value}")
        @NotNull(message = "Amount field must not be empty")
        BigDecimal amount,

        @NotNull(message = "Currency field must not be empty")
        Currency currency,

        @NotNull(message = "Id campaign field must not be empty")
        Long campaignId,

        LocalDateTime donationTime
) {
}
