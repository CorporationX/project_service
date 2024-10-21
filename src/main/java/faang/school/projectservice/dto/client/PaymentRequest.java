package faang.school.projectservice.dto.client;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest (
        @NotNull(message = "Payment number must not be null")
        @Positive(message = "Payment number must be positive")
        Long paymentNumber,

        @NotNull(message = "Amount must not be null")
        @Min(value = 1, message = "Amount must be at least 1")
        BigDecimal amount,

        @NotNull(message = "Payment currency must not be null")
        Currency paymentCurrency,

        @NotNull(message = "Target currency must not be null")
        Currency targetCurrency
) {}
