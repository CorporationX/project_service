package faang.school.projectservice.dto.filter;

import faang.school.projectservice.model.CampaignStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CampaignFilterDto extends Page {
    private String namePattern;
    private BigDecimal minGoal;
    private BigDecimal maxGoal;
    private CampaignStatus status;
}
