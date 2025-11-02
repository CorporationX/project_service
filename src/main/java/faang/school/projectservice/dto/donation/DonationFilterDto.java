package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import jakarta.validation.constraints.AssertTrue;
import lombok.Builder;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@FieldNameConstants
@Builder
public record DonationFilterDto(
        LocalDateTime donationTime,
        Currency currency,
        BigDecimal amountMin,
        BigDecimal amountMax
) {
    @AssertTrue(message = "At least one filter criteria must be provided")
    public boolean isAtLeastOneFilterPresent() {
        return donationTime != null || currency != null || amountMin != null || amountMax != null;
    }
}