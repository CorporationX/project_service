package faang.school.projectservice.dto.client;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentResponse(
        String status,
        int verificationCode,
        Long paymentNumber,
        BigDecimal amount,
        Currency currency,
        String message
) {
}
