package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DonationCreateDto {
    @NotNull(message = "paymentNumber cannot be null")
    private Long paymentNumber;

    @NotNull(message = "amount cannot be null")
    private BigDecimal amount;

    @NotNull(message = "campaignId cannot be null")
    private Long campaignId;

    private Currency currency;
}
