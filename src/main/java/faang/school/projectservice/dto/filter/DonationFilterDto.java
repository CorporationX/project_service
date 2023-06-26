package faang.school.projectservice.dto.filter;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.CampaignStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DonationFilterDto extends Page {
    private String namePattern;
    private Long campaignId;
    private BigDecimal minDonateSum;
    private BigDecimal maxDonateSum;
    private Currency currency;
    private CampaignStatus status;
}
