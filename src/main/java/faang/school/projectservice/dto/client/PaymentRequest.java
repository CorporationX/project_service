package faang.school.projectservice.dto.client;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public record PaymentRequest (
        Long paymentNumber,

        @Min(1)
        @NotBlank(message = "amount should not be blank")
        BigDecimal amount,

        @NotBlank(message = "paymentCurrency should not be blank")
        Currency paymentCurrency,

        @NotNull
        Currency targetCurrency
) {}
