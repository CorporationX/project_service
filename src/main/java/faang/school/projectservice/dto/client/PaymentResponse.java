package faang.school.projectservice.dto.client;

import java.math.BigDecimal;

public record PaymentResponse(
        PaymentStatus status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        Currency paymentCurrency,
        Currency targetCurrency,
        String message
) {
}
