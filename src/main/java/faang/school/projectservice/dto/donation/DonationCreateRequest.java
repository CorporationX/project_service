package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DonationCreateRequest {

    @Positive
    @NotNull
    private BigDecimal amount;

    @NotNull
    private Currency currency;
}
