package faang.school.projectservice.dto.client;

import java.math.BigDecimal;

public record PaymentResponseDto(
        String status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        CurrencyDto paymentCurrencyDto,
        CurrencyDto targetCurrencyDto,
        String message
) {
}
