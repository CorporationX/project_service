package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
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
    public Long paymentNumber;
    public BigDecimal amount;
    public LocalDateTime donationTime;
    public Long campaignId;
    public Currency currency;
    public Long userId;
}
