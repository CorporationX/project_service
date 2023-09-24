package faang.school.projectservice.dto.client;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
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
