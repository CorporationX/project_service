package faang.school.projectservice.dto.client;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(
        Long paymentNumber,

        @Min(1)
        @NotNull
        BigDecimal amount,

        @NotNull
        Currency paymentCurrency,

        @NotNull
        Currency targetCurrency
) {
}
