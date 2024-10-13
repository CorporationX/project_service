package faang.school.projectservice.model.dto;

import java.math.BigDecimal;

import faang.school.projectservice.model.enums.Currency;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PaymentRequestDto(
        Long paymentNumber,

        @Min(1)
        @NotNull
        BigDecimal amount,

        @NotNull
        Currency paymentCurrency,

        @NotNull
        Currency targetCurrency
) {}
