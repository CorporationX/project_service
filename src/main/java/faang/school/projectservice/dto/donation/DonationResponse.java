package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.client.Currency;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DonationResponse {

    private long id;
    private long paymentNumber;
    private BigDecimal amount;
    private LocalDateTime donationTime;
    private CampaignDto campaign;
    private Currency currency;
}
