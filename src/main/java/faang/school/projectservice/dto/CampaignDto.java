package faang.school.projectservice.dto;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.CampaignStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CampaignDto {
    private Long id;
    private String title;
    private CampaignStatus status;
    private Currency currency;
    private BigDecimal goal;
    private BigDecimal amountRaised;
    private String description;
    private Long projectId;
}
