package faang.school.projectservice.dto.donation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DonationToSendDto {
    private long paymentNumber;
    @Positive(message = "Amount cannot be negative")
    private BigDecimal amount;
    private long campaignId;
    @NotNull(message = "Currency cannot be empty")
    private String currency;
}
