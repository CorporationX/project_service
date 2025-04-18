package faang.school.projectservice.dto.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentResponse(
        String status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        @JsonProperty("currency")
        Currency paymentCurrency,
        Currency targetCurrency,
        String message
) {
}
