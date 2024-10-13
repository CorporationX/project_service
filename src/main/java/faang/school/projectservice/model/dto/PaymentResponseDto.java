package faang.school.projectservice.model.dto;

import faang.school.projectservice.model.enums.Currency;

import java.math.BigDecimal;

public record PaymentResponseDto(
        String status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        Currency paymentCurrency,
        Currency targetCurrency,
        String message
) {
}