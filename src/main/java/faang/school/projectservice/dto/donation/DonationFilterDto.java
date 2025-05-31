package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class DonationFilterDto {
    public LocalDateTime createdAt;
    public Currency currency;
    public BigDecimal minAmount;
    public BigDecimal maxAmount;
}
