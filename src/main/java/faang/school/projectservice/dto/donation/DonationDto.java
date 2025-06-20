package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Donation DTO")
public class DonationDto {
    @NotNull
    @Schema(description = "Number of payment")
    public Long paymentNumber;
    @NotNull
    @Min(1)
    @Schema(description = "Donation payment amount")
    public BigDecimal amount;
    @NotNull
    @Schema(description = "Campaign id")
    public Long campaignId;
    @NotNull
    @Schema(description = "Donation currency", example = "USD", allowableValues = {"USD", "EUR"})
    public Currency currency;
    @NotNull
    @Schema(description = "Donator user id")
    public Long userId;

    public LocalDateTime donationTime;
}
