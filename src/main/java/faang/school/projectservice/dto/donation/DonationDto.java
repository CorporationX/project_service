package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class DonationDto {
    private Long id;
    private Long paymentNumber;
    private BigDecimal amount;
    private LocalDateTime donationTime;
    private Long campaignId;
    private Currency currency;
    private Long userId;
}
