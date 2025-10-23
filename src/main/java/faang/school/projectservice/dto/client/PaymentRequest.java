package faang.school.projectservice.dto.client;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentRequest (
        Long paymentNumber,

        @Min(1)
        @NotNull(message = "Amount cannot be empty")
        @Positive(message = "Amount size cannot be negative")
        BigDecimal amount,

        @NotNull(message = "Payment currency cannot be empty")
        Currency paymentCurrency,

        @NotNull(message = "Target currency cannot be empty")
        Currency targetCurrency
) {
}
