package faang.school.projectservice.dto.client;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest (
        Long paymentNumber,

        @Min(value = 1, message = "Amount must be greater than 0")
        @NotNull (message = "Amount must not be null")
        BigDecimal amount,

        @NotNull (message = "Payment currency must not be null")
        Currency paymentCurrency,

        @NotNull (message = "Target currency must not be null")
        Currency targetCurrency
) {
}
