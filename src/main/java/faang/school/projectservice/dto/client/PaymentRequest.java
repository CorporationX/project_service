package faang.school.projectservice.dto.client;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentRequest {

        @NotNull
        private final Long paymentNumber;

        @Min(1)
        @NotNull
        private final BigDecimal amount;

        @NotNull
        private final Currency paymentCurrency;

        @NotNull
        private Currency targetCurrency;
}
