package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DonationFilterDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Currency currency;
    private BigDecimal maxAmount;
    private BigDecimal minAmount;
}
