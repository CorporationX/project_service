package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.dto.client.Currency;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateCampaignDto {
    private String title;
    private String description;
    private BigDecimal goal;
    private Long projectId;
    private Currency currency;
    private Long createdBy;
}
