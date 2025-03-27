package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.filter.donation.Value;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DonationFilterDto {
    private LocalDateTime donationTime;
    private Currency currency;
    private Value value;
}
