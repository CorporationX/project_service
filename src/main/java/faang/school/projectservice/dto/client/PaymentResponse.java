package faang.school.projectservice.dto.client;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentResponse(
        String status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        Currency paymentCurrency,
        Currency targetCurrency,
        String message
) {
}
