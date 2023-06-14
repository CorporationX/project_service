package faang.school.projectservice.dto;

import faang.school.projectservice.dto.client.Currency;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CampaignCreateDto {
    private String title;
    private String description;
    private Long projectId;
    private BigDecimal goal;
    private Currency currency;
}
