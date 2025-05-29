package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
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
public class DonationDto {
    @NotNull
    public Long paymentNumber;
    @NotNull
    @Min(1)
    public BigDecimal amount;
    @NotNull
    public Long campaignId;
    @NotNull
    public Currency currency;
    @NotNull
    public Long userId;

    public LocalDateTime donationTime;
}
