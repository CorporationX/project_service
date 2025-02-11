package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import lombok.Data;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Data
public class DonationFilterDto {
    private Currency currency;
    private BigDecimal maxAmount;
    private BigDecimal minAmount;
    private LocalDateTime donationDate;
}
