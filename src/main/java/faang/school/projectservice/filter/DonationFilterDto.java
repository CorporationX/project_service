package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record DonationFilterDto(
        LocalDateTime startDate,
        LocalDateTime endDate,
        @NotNull
        Currency currency,
        @Positive
        BigDecimal minAmount,
        @Positive
        BigDecimal maxAmount
) {}
