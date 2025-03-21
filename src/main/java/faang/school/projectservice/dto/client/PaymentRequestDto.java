package faang.school.projectservice.dto.client;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequestDto(
        Long paymentNumber,

        @Min(1)
        @NotNull
        BigDecimal amount,

        @NotNull
        CurrencyDto paymentCurrencyDto,

        @NotNull
        CurrencyDto targetCurrencyDto
) {
}
