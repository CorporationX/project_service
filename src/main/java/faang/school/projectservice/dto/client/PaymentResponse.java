package faang.school.projectservice.dto.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentResponse(
        @NotBlank(message = "Status should not be blank") String status,
        @NotNull(message = "Verification code must be provided") int verificationCode,
        @NotNull(message = "Payment number must be provided") long paymentNumber,
        @NotNull(message = "Amount must be provided") @Positive(message = "Amount must be positive") BigDecimal amount,
        @NotNull(message = "Payment currency must be provided") Currency paymentCurrency,
        @NotNull(message = "Target currency must be provided") Currency targetCurrency,
        @NotBlank(message = "Message should not be blank") String message
) {
}