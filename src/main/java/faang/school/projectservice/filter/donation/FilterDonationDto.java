package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.client.Currency;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FilterDonationDto {
    private Currency currencyPattern;
    private BigDecimal maxAmountPattern;
    private BigDecimal minAmountPattern;
}
