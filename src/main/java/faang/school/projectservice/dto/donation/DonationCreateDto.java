package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DonationCreateDto {
    @NotNull(message = "Amount mast not be null")
    @Min(value = 1, message = "Amount must be greater than 0")
    private BigDecimal amount;
    @NotNull(message = "Campaign id must not be null")
    @Positive
    private Long campaignId;
    @NotNull(message = "Currency must not be null")
    private Currency currency;
    @NotNull(message = "User id must not be null")
    @Positive
    private Long userId;
}
