package faang.school.projectservice.dto.donation;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DonationDto {
    private long paymentNumber;
    private BigDecimal amount;
    private LocalDateTime donationTime;
    private long campaignId;
    private String currency;
    private long userId;
}
