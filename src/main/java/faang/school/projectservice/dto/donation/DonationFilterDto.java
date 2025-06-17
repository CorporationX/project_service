package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Donation filters")
public class DonationFilterDto {
    @Schema(description = "Donation created at filter", example = "15.06.2025 14:30", pattern = "dd.MM.yyyy HH:mm")
    public LocalDateTime createdAt;
    @Schema(description = "Donation currency filter", example = "USD")
    public Currency currency;
    @Schema(description = "Min donation filter amount")
    public BigDecimal minAmount;
    @Schema(description = "Max donation filter amount")
    public BigDecimal maxAmount;
}
