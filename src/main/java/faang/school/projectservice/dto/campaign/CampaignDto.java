package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.CampaignStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CampaignDto {
    private long id;
    private String title;
    private String description;
    private BigDecimal goal;
    private BigDecimal amountRaised;
    private CampaignStatus status;
    private long projectId;
    private Currency currency;
    private long createdBy;
}
