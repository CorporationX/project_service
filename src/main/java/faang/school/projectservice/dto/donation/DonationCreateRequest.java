package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DonationCreateRequest {

    @Min(1)
    @NotNull
    private BigDecimal amount;

    private long campaignId;

    @NotNull
    private Currency currency;

    private long userId;
}
