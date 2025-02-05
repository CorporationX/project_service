package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DonationFilterDto {
    private LocalDate date;
    private Currency currency;
    private BigDecimal maxAmount;
    private BigDecimal minAmount;
}
