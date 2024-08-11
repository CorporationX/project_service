package faang.school.projectservice.dto.client;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
@Validated
public record PaymentResponse(
        @NotBlank(message = "status should not be blank")
        String status,
        int verificationCode,
        long paymentNumber,
        @NotBlank(message = "amount should not be blank")
        BigDecimal amount,
        Currency paymentCurrency,
        Currency targetCurrency,
        String message
) {
}
