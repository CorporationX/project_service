package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DonationViewDto {
    @NotNull(message = "id cannot be null")
    private Long id;

    private Long paymentNumber;

    @NotNull(message = "amount cannot be null")
    private BigDecimal amount;

    private LocalDateTime donationTime;

    @NotNull(message = "campaignId cannot be null")
    private Long campaignId;

    private Currency currency;

    @NotNull(message = "userId cannot be null")
    private Long userId;
}
